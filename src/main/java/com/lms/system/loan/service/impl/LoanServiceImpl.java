package com.lms.system.loan.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.generic.exception.BadRequestException;
import com.lms.generic.exception.NotFoundException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.user.model.User;
import com.lms.system.loan.dto.LoanRequestDTO;
import com.lms.system.loan.enums.LoanRiskCategory;
import com.lms.system.loan.enums.LoanStatus;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
\import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

        List<LoanInstallment> unpaidInstallments = loanInstallmentRepository.findLoanInstallmentByLoansAndStatus(activeLoans, PaymentStatus.NOT_PAID);

        if (unpaidInstallments.isEmpty()) {
            log.info("No unpaid installments to process.");
            return;
        }

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

}
