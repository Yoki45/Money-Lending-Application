package com.lms.system.notification.kafka.consumer.loan.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.system.customer.account.enums.ActiveStatus;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.user.model.User;
import com.lms.system.loan.dto.LoanRequestDTO;
import com.lms.system.loan.enums.LoanStatus;
import com.lms.system.loan.enums.LoanType;
import com.lms.system.loan.enums.PaymentStatus;
import com.lms.system.loan.model.CreditScore;
import com.lms.system.loan.model.Loan;
import com.lms.system.loan.model.LoanInstallment;
import com.lms.system.loan.repository.CreditScoreRepository;
import com.lms.system.loan.repository.LoanInstallmentRepository;
import com.lms.system.loan.repository.LoanLimitRepository;
import com.lms.system.loan.repository.LoanRepository;
import com.lms.system.notification.kafka.consumer.loan.ILoanConsumerService;
import com.lms.system.notification.messaging.email.service.IEmailService;
import com.lms.system.product.enums.TenureType;
import com.lms.system.product.model.Product;
import com.lms.system.product.model.ProductFee;
import com.lms.system.product.repository.ProductFeeRepository;
import com.lms.system.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.lms.utils.Utils.MINIMUM_CREDIT_SCORE;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoanConsumerServiceImpl implements ILoanConsumerService {


    private final ObjectMapper objectMapper;

    private final LoanLimitRepository loanLimitRepository;

    private final LoanRepository loanRepository;

    private final AccountRepository accountRepository;

    private final CreditScoreRepository creditScoreRepository;

    private final ProductRepository productRepository;

    private final ProductFeeRepository productFeeRepository;

    private final LoanInstallmentRepository loanInstallmentRepository;

    private final IEmailService emailService;

    @Override
    public void processLoanRequest(LoanRequestDTO request) throws JsonProcessingException {

        log.info("Received message mapped : {}", request.toString());

        try {

            Account account = accountRepository.findAccountByAccountNumber(request.getAccountNumber());

            User user = account.getCustomer();

            Product product = productRepository.findById(Long.valueOf(request.getProductId()))
                    .orElse(null);

            CreditScore creditScore = creditScoreRepository.findCreditScoreByUser(user);

            if (creditScore == null || creditScore.getScore() < MINIMUM_CREDIT_SCORE) {
                emailService.sendEmailRejectionDueToLowCreditScore(user, creditScore);
                log.info("loan request rejected due to low credit score : {}", creditScore);
                return;
            }


            List<ProductFee> fees = productFeeRepository.findByProduct(product);
            List<ProductFee> disbursementFees = fees.stream()
                    .filter(fee -> fee.getApplyOnDisbursement() && fee.getActiveStatus().equals(ActiveStatus.ACTIVE))
                    .collect(Collectors.toList());


            double feeTotal = calculateDisbursementFees(request.getAmount(), disbursementFees);
            double loanBalance = request.getAmount() + feeTotal;

            LocalDate firstDueDate = calculateDueDate(product.getTenureType(), product.getTenureValue());

            Loan loan = Loan.builder()
                    .account(account)
                    .amount(request.getAmount())
                    .balance(loanBalance)
                    .dueDate(Date.valueOf(firstDueDate))
                    .status(LoanStatus.OPEN)
                    .product(product)
                    .loanType(LoanType.DEFAULT)
                    .build();

           loan = loanRepository.save(loan);

            if (request.getNumberOfInstallments() > 1) {
                List<LoanInstallment> installments = createInstallmentsForLoan(loan, product, request.getNumberOfInstallments());
                loanInstallmentRepository.saveAll(installments);
                log.info("installments : {}", installments);

            }

            emailService.sendLoanApprovalEmail(request, user, firstDueDate);

        } catch (Exception e) {
            log.error("Error processing loan for account {}: {}", request.getAccountNumber(), e.getMessage(), e);
        }
    }


    private LocalDate calculateDueDate(TenureType type, Integer value) {
        return type == TenureType.MONTHS ? LocalDate.now().plusMonths(value) : LocalDate.now().plusDays(value);
    }

    private double calculateDisbursementFees(Double principal, List<ProductFee> fees) {
        return fees.stream().mapToDouble(fee ->
                Boolean.TRUE.equals(fee.getIsPercentage()) ?
                        (principal * fee.getAmount() / 100) :
                        fee.getAmount()
        ).sum();
    }


    private List<LoanInstallment> createInstallmentsForLoan(Loan loan, Product product, int count) {
        List<LoanInstallment> loanInstallments = new ArrayList<>();

        double installmentAmount = loan.getBalance() / count;
        LocalDate start = LocalDate.now();

        for (int i = 1; i <= count; i++) {
            LocalDate dueDate = product.getTenureType() == TenureType.MONTHS
                    ? start.plusMonths(i * product.getTenureValue())
                    : start.plusDays(i * product.getTenureValue());

            LoanInstallment installment = LoanInstallment.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .amount(installmentAmount)
                    .balance(installmentAmount)
                    .dueDate(Date.valueOf(dueDate))
                    .status(LoanStatus.OPEN)
                    .paymentStatus(PaymentStatus.NOT_PAID)
                    .build();

            loanInstallments.add(installment);

        }
        return loanInstallments;
    }

}
