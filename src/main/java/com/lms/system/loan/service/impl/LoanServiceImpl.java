package com.lms.system.loan.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.generic.dateRange.model.DateRangeFilter;
import com.lms.generic.dateRange.service.DateFilterRangeService;
import com.lms.generic.exception.BadRequestException;
import com.lms.generic.exception.NotFoundException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.user.model.User;
import com.lms.system.loan.dto.LoanInstallmentDTO;
import com.lms.system.loan.dto.LoanReportDTO;
import com.lms.system.loan.dto.LoanRequestDTO;
import com.lms.system.loan.dto.LoanResponseDTO;
import com.lms.system.loan.enums.LoanRiskCategory;
import com.lms.system.loan.enums.LoanStatus;
import com.lms.system.loan.enums.LoanType;
import com.lms.system.loan.enums.PaymentStatus;
import com.lms.system.loan.model.Loan;
import com.lms.system.loan.model.LoanInstallment;
import com.lms.system.loan.model.LoanLimit;
import com.lms.system.loan.repository.LoanInstallmentRepository;
import com.lms.system.loan.repository.LoanLimitRepository;
import com.lms.system.loan.repository.LoanRepository;
import com.lms.system.loan.service.ILoanService;
import com.lms.system.notification.messaging.africastalking.service.AfricasTalkingGateway;
import com.lms.system.product.enums.FeeType;
import com.lms.system.product.model.Product;
import com.lms.system.product.model.ProductFee;
import com.lms.system.product.repository.ProductRepository;
import com.lms.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoanServiceImpl implements ILoanService {

    private final LoanRepository loanRepository;

    private final ILocalizationService localizationService;

    private final AccountRepository accountRepository;

    private final LoanLimitRepository loanLimitRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    private final ProductRepository productRepository;

    private final LoanInstallmentRepository loanInstallmentRepository;

    private final AfricasTalkingGateway africasTalkingGateway;

    private final DateFilterRangeService dateFilterRangeService;


    @Override
    public String requestForLoan(LoanRequestDTO loanRequestDTO) throws JsonProcessingException {

        if (loanRequestDTO == null) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails", null));
        }

        Product product = productRepository.findById(Long.valueOf(loanRequestDTO.getProductId()))
                .orElseThrow(() ->
                        new NotFoundException(localizationService.getMessage("message.product.NotFound", null)));


        List<Loan> overdueLoans = loanRepository.
                findLoanByAccountNumberAndProduct(loanRequestDTO.getAccountNumber(), loanRequestDTO.getProductId(), new ArrayList<>());

        if (overdueLoans.size() > 0) {
            throw new BadRequestException(localizationService.getMessage("message.loans.pendingOverdue", null));
        }

        User user = accountRepository.findCustomerByAccountNumber(loanRequestDTO.getAccountNumber());

        LoanLimit loanLimit = loanLimitRepository.findLoanLimitByUser(user);

        Double existingLoans = loanRepository.totalLoansByAccountNumber(loanRequestDTO.getAccountNumber(), new ArrayList<>());

        if (loanLimit == null || loanLimit.getCategory() == LoanRiskCategory.INELIGIBLE) {
            throw new BadRequestException(localizationService.getMessage("message.loan.enligible", null));
        }

        if (loanLimit.getLimit() <= (existingLoans + loanRequestDTO.getAmount())) {
            throw new BadRequestException(localizationService.getMessage("message.loans.exceededLoanLimit", null));
        }


        String message = mapper.writeValueAsString(loanRequestDTO);
        kafkaTemplate.send("loan_creation", message);
        log.info("Loan application published for account {}: {}", loanRequestDTO.getAccountNumber(), message);

        return localizationService.getMessage("message.loans.created", null);
    }

    @Override
    @Transactional
    public void sweepOverdueLoans() {
        Date today = new Date();

        List<Loan> activeLoans = loanRepository.findLoanByStatusAndDate(LoanStatus.OPEN, today);
        if (activeLoans.isEmpty()) {
            log.info("No active loans found for overdue sweep.");
            return;
        }

        log.info("overdue sweep found for active loans {}", activeLoans.size());

        List<LoanInstallment> unpaidInstallments = loanInstallmentRepository.findLoanInstallmentByLoansAndStatus(activeLoans, PaymentStatus.NOT_PAID);

        if (unpaidInstallments.isEmpty()) {
            log.info("No unpaid installments to process.");
            return;
        }

        log.info("unpaid installments to process {}", unpaidInstallments.size());


        Map<Loan, List<LoanInstallment>> installmentsByLoan = unpaidInstallments.stream()
                .collect(Collectors.groupingBy(LoanInstallment::getLoan));

        for (Loan loan : activeLoans) {
            List<LoanInstallment> installments = installmentsByLoan.getOrDefault(loan, List.of());

            if (installments.isEmpty()) {
                loan.setStatus(LoanStatus.OVERDUE);
                loanRepository.save(loan);
                continue;
            }

            List<LoanInstallment> pastDueInstallments = installments.stream()
                    .filter(i -> i.getDueDate().before(today))
                    .toList();

            if (pastDueInstallments.isEmpty()) {
                continue;
            }

            for (LoanInstallment overdue : pastDueInstallments) {
                overdue.setStatus(LoanStatus.OVERDUE);
                loanInstallmentRepository.save(overdue);
            }

            boolean allUnpaidArePastDue = installments.stream()
                    .allMatch(i -> i.getDueDate().before(today));

            if (allUnpaidArePastDue) {
                loan.setStatus(LoanStatus.OVERDUE);
                loanRepository.save(loan);
            }
        }

        log.info("overdue sweep found for active loans {} complete", activeLoans.size());
    }

    @Override
    @Transactional
    public void applyLateFeesToOvedueLoans() {

        List<Loan> overdueLoans = loanRepository.findLoanByStatus(LoanStatus.OVERDUE);

        for (Loan loan : overdueLoans) {
            List<ProductFee> lateFees = loan.getProduct().getFees().stream()
                    .filter(f -> f.getFeeType() == FeeType.LATE)
                    .toList();

            for (ProductFee fee : lateFees) {
                LocalDate due = loan.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (ChronoUnit.DAYS.between(due, LocalDate.now()) >= fee.getTriggerDaysAfterDue()) {
                    double amount = fee.getIsPercentage() ? loan.getBalance() * (fee.getAmount() / 100.0) : fee.getAmount();
                    loan.setBalance(loan.getBalance() + amount);
                    loanRepository.save(loan);
                }
            }

        }
    }


    @Transactional
    public void sendDueDateReminders() {
        Date reminderDate = Date.from(LocalDate.now()
                .plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<Loan> upcomingDueLoans = loanRepository.findLoanByStatusAndDate(LoanStatus.OPEN, reminderDate);

        List<User> customers = new ArrayList<>();
        for (Loan loan : upcomingDueLoans) {
            if (loan.getStatus() == LoanStatus.OPEN && loan.getBalance() > 0) {
                customers.add(loan.getAccount().getCustomer());
            }
        }
        if (!customers.isEmpty()) {
            africasTalkingGateway.sentReminderMessages(customers);
        }
    }

    @Override
    public LoanReportDTO fetchLoanDetails(LoanStatus status, Long loanId, String range, Long customerId, Long productId, Long accountNumber, Integer page, LoanType loanType) {

        int currentPage = Optional.ofNullable(page).orElse(1);
        String dateRange = Optional.ofNullable(range).orElse("this_week");

        DateRangeFilter dateRangeFilter = dateFilterRangeService.getFilterDateRange(dateRange);

        List<Loan> loans = loanRepository.getLoans(status, loanId, dateRangeFilter.getBeginCalendar().getTime(),
                dateRangeFilter.getEndCalendar().getTime(), customerId, productId, accountNumber, loanType);

        List<List<Loan>> pagedLoans = Utils.createSubList(loans, Utils.MAX_PAGE_SIZE);
        if (pagedLoans.isEmpty()) {
            return new LoanReportDTO(currentPage, 0, 0d, 0d, Collections.emptyList(), 0d);
        }

        List<Loan> currentPageLoans = pagedLoans.get(currentPage - 1);
        Map<Loan, List<LoanInstallment>> loanInstallmentMap = loanInstallmentRepository
                .findLoanInstallmentByLoans(currentPageLoans)
                .stream()
                .collect(Collectors.groupingBy(LoanInstallment::getLoan));

        List<LoanResponseDTO> loanResponses = currentPageLoans.stream()
                .map(loan -> toLoanResponseDTO(loan, loanInstallmentMap.getOrDefault(loan, List.of())))
                .toList();

        double totalOverdueAmount = loans.stream()
                .filter(l -> l.getStatus() == LoanStatus.OVERDUE)
                .mapToDouble(Loan::getAmount)
                .sum();

        double totalClosedAmount = loans.stream()
                .filter(l -> l.getStatus() == LoanStatus.CLOSED)
                .mapToDouble(Loan::getAmount)
                .sum();

        double totalActiveBalance = loans.stream()
                .filter(l -> l.getStatus() == LoanStatus.OPEN || l.getStatus() == LoanStatus.OVERDUE)
                .mapToDouble(Loan::getAmount)
                .sum();

        return new LoanReportDTO(currentPage, pagedLoans.size(), totalOverdueAmount, totalClosedAmount, loanResponses, totalActiveBalance);
    }

    @Override
    public String consolidateLoanDueDates(LoanRequestDTO loanRequestDTO) {

        if (loanRequestDTO == null) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails", null));
        }

        Long accountNumber = loanRequestDTO.getAccountNumber();
        LocalDate consolidatedStartDate = Instant.ofEpochMilli(loanRequestDTO.getConsolidateDueDate())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();


        List<Loan> activeLoans = loanRepository.findLoanByAccountNumberAndStatus(accountNumber, Arrays.asList(LoanStatus.OPEN));
        if (activeLoans.isEmpty()) {
            return localizationService.getMessage("message.loan.noActiveFound", null);
        }

        Map<Loan, List<LoanInstallment>> loanInstallmentMap = loanInstallmentRepository
                .findLoanInstallmentByLoans(activeLoans)
                .stream()
                .filter(installment -> installment.getLoan() != null && installment.getPaymentStatus() == PaymentStatus.NOT_PAID)
                .collect(Collectors.groupingBy(LoanInstallment::getLoan));

        for (Loan loan : activeLoans) {
            List<LoanInstallment> unpaidInstallments = loanInstallmentMap.getOrDefault(loan, List.of());

            if (!unpaidInstallments.isEmpty()) {
                // Sort installments by original due date to maintain order
                unpaidInstallments.sort(Comparator.comparing(LoanInstallment::getDueDate));

                for (int i = 0; i < unpaidInstallments.size(); i++) {
                    LocalDate newDueDate = consolidatedStartDate.plusMonths(i);
                    unpaidInstallments.get(i).setDueDate(Date.from(newDueDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    loanInstallmentRepository.save(unpaidInstallments.get(i));
                }

                // Set loan's due date to the last updated installment due date
                loan.setDueDate(unpaidInstallments.get(unpaidInstallments.size() - 1).getDueDate());
                loan.setLoanType(LoanType.CONSOLIDATED);
            } else {
                // If no installments, use base consolidated start date as due date
                loan.setDueDate(Date.from(consolidatedStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                loan.setLoanType(LoanType.CONSOLIDATED);
            }

            loanRepository.save(loan);
        }

        log.info("consolidation complete for account {}",accountNumber);

        return localizationService.getMessage("message.loan.consolidated", null);

    }

    private LoanResponseDTO toLoanResponseDTO(Loan loan, List<LoanInstallment> installments) {
        List<LoanInstallmentDTO> installmentDTOs = installments.stream()
                .map(installment -> {
                    LoanInstallmentDTO dto = new LoanInstallmentDTO();
                    dto.setInstallmentId(installment.getId());
                    dto.setInstallmentNumber(installment.getInstallmentNumber());
                    dto.setAmount(installment.getAmount());
                    dto.setDueDate(installment.getDueDate());
                    return dto;
                })
                .toList();

        LoanResponseDTO responseDTO = new LoanResponseDTO();
        responseDTO.setId(loan.getId());
        responseDTO.setProductId(loan.getProduct().getId());
        responseDTO.setProductName(loan.getProduct().getName());
        responseDTO.setAmount(loan.getAmount());
        responseDTO.setBalance(loan.getBalance());
        responseDTO.setDueDate(loan.getDueDate());
        responseDTO.setStatus(loan.getStatus());
        responseDTO.setInstallments(installmentDTOs);
        responseDTO.setLoanType(loan.getLoanType() != null ? loan.getLoanType() : LoanType.DEFAULT);


        return responseDTO;
    }


}
