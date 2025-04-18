package com.lms.system.notifications.kafka.consumer.loan.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.generic.exception.NotFoundException;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.user.model.User;
import com.lms.system.loans.dto.LoanRequestDTO;
import com.lms.system.loans.enums.LoanStatus;
import com.lms.system.loans.enums.PaymentStatus;
import com.lms.system.loans.model.CreditScore;
import com.lms.system.loans.model.Loan;
import com.lms.system.loans.model.LoanInstallment;
import com.lms.system.loans.repository.CreditScoreRepository;
import com.lms.system.loans.repository.LoanInstallmentRepository;
import com.lms.system.loans.repository.LoanLimitRepository;
import com.lms.system.loans.repository.LoanRepository;
import com.lms.system.notifications.kafka.consumer.loan.ILoanConsumerService;
import com.lms.system.product.enums.TenureType;
import com.lms.system.product.model.Product;
import com.lms.system.product.model.ProductFee;
import com.lms.system.product.repository.ProductFeeRepository;
import com.lms.system.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static com.lms.utils.Utils.MINIMUM_CREDIT_SCORE;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoanConsumerServiceImpl implements ILoanConsumerService {


    private final ObjectMapper objectMapper;

    private final LoanLimitRepository loanLimitRepository;

    private final LoanRepository loanRepository;

    private final AccountRepository accountRepository;

    private final CreditScoreRepository creditScoreRepository;

    private final ProductRepository productRepository;

    private final ProductFeeRepository productFeeRepository;

    private final LoanInstallmentRepository loanInstallmentRepository;

    @Override
    public void processLoanRequest(String message) throws JsonProcessingException {

        log.info("Received message: {}", message);

        LoanRequestDTO request = objectMapper.readValue(message, LoanRequestDTO.class);

        log.info("Received message mapped : {}", request.toString());

        try {

            Account account = accountRepository.findAccountByAccountNumber(request.getAccountNumber());

            User user = account.getCustomer();

            Product product = productRepository.findById(Long.valueOf(request.getProductId()))
                    .orElse(null);

            CreditScore creditScore = creditScoreRepository.findCreditScoreByUser(user);

            if (creditScore == null || creditScore.getScore() < MINIMUM_CREDIT_SCORE) {
                notifyUser(user, "Loan rejected: your credit score is too low.");
                return;
            }


            List<ProductFee> fees = productFeeRepository.findByProduct(product);
            List<ProductFee> disbursementFees = fees.stream()
                    .filter(ProductFee::getApplyOnDisbursement)
                    .toList();

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
                    .build();
            loanRepository.save(loan);

            createInstallmentsForLoan(loan, product, request.getNumberOfInstallments());

            notifyUser(user, "Loan approved! Amount: " + request.getAmount() + ". Due by: " + firstDueDate);

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


    private void createInstallmentsForLoan(Loan loan, Product product, int count) {
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
                    .dueDate(Date.valueOf(dueDate))
                    .paymentStatus(PaymentStatus.NOT_PAID)
                    .build();

            loanInstallmentRepository.save(installment); // Assume repo exists
        }
    }


    private void notifyUser(User user, String message) {
        // You can publish to Kafka or call NotificationService
//        NotificationEvent event = NotificationEvent.builder()
//                .userId(user.getId())
//                .channel("SMS") // could be dynamic
//                .message(message)
//                .build();
//        kafkaTemplate.send("notifications", event);
    }

}
