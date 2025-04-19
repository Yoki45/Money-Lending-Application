package com.lms.loan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.generic.exception.BadRequestException;
import com.lms.generic.exception.NotFoundException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.customer.account.model.Account;
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
import com.lms.system.loan.service.impl.LoanServiceImpl;
import com.lms.system.notification.messaging.africastalking.service.AfricasTalkingGateway;
import com.lms.system.product.enums.FeeType;
import com.lms.system.product.enums.TenureType;
import com.lms.system.product.model.Product;
import com.lms.system.product.model.ProductFee;
import com.lms.system.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanServiceImplTest {


    @Mock
    private ProductRepository productRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LoanLimitRepository loanLimitRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private ILocalizationService localizationService;

    @Mock
    private AfricasTalkingGateway africasTalkingGateway;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    private LoanRequestDTO validRequest;
    private Product product;
    private User user;
    private LoanLimit loanLimit;

    @BeforeEach
    void setup() {
        validRequest = LoanRequestDTO.builder()
                .accountNumber(123456789L)
                .productId(1)
                .amount(1000.0)
                .build();

        product = Product.builder()
                .id(1L)
                .name("30-day loan")
                .tenureValue(30)
                .tenureType(TenureType.DAYS)
                .build();

        user = User.builder().id(1L).username("john.doe").build();

        loanLimit = LoanLimit.builder()
                .limit(5000.0)
                .category(LoanRiskCategory.MEDIUM)
                .customer(user)
                .build();
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void requestForLoan_shouldPublishToKafka_whenValid() throws Exception {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(loanRepository.findLoanByAccountNumberAndProduct(any(), any(), any())).thenReturn(Collections.emptyList());
        when(accountRepository.findCustomerByAccountNumber(anyLong())).thenReturn(user);
        when(loanLimitRepository.findLoanLimitByUser(user)).thenReturn(loanLimit);
        when(loanRepository.totalLoansByAccountNumber(any(), any())).thenReturn(2000.0);
        when(mapper.writeValueAsString(any())).thenReturn("loan-json");
        when(localizationService.getMessage("message.loans.created", null)).thenReturn("Loan created");

        String result = loanService.requestForLoan(validRequest);

        assertEquals("Loan created", result);
        verify(kafkaTemplate).send(eq("loan_creation"), anyString());
    }

    @Test
    void requestForLoan_shouldThrow_whenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> loanService.requestForLoan(validRequest));
    }

    @Test
    void requestForLoan_shouldThrow_whenOverdueLoansExist() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(loanRepository.findLoanByAccountNumberAndProduct(any(), any(), any())).thenReturn(List.of(new Loan()));
        when(localizationService.getMessage("message.loans.pendingOverdue", null)).thenReturn("Overdue loans");

        assertThrows(BadRequestException.class, () -> loanService.requestForLoan(validRequest));
    }

    @Test
    void requestForLoan_shouldThrow_whenUserIsIneligible() {
        loanLimit.setCategory(LoanRiskCategory.INELIGIBLE);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(loanRepository.findLoanByAccountNumberAndProduct(any(), any(), any())).thenReturn(Collections.emptyList());
        when(accountRepository.findCustomerByAccountNumber(anyLong())).thenReturn(user);
        when(loanLimitRepository.findLoanLimitByUser(user)).thenReturn(loanLimit);
        when(localizationService.getMessage("message.loan.enligible", null)).thenReturn("Ineligible");

        assertThrows(BadRequestException.class, () -> loanService.requestForLoan(validRequest));
    }

    @Test
    void requestForLoan_shouldThrow_whenExceedingLoanLimit() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(loanRepository.findLoanByAccountNumberAndProduct(any(), any(), any())).thenReturn(Collections.emptyList());
        when(accountRepository.findCustomerByAccountNumber(anyLong())).thenReturn(user);
        when(loanLimitRepository.findLoanLimitByUser(user)).thenReturn(loanLimit);
        when(loanRepository.totalLoansByAccountNumber(any(), any())).thenReturn(4900.0);
        when(localizationService.getMessage("message.loans.exceededLoanLimit", null)).thenReturn("Limit exceeded");

        assertThrows(BadRequestException.class, () -> loanService.requestForLoan(validRequest));
    }

    @Test
    void sweepOverdueLoans_shouldMarkLoansAndInstallmentsOverdue() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus(LoanStatus.OPEN);
        loan.setDueDate(new Date(System.currentTimeMillis() - 86400000)); // yesterday
        loan.setBalance(500.0);

        LoanInstallment installment = new LoanInstallment();
        installment.setLoan(loan);
        installment.setDueDate(new Date(System.currentTimeMillis() - 86400000));
        installment.setPaymentStatus(PaymentStatus.NOT_PAID);

        when(loanRepository.findLoanByStatusAndDate(eq(LoanStatus.OPEN), any()))
                .thenReturn(List.of(loan));
        when(loanInstallmentRepository.findLoanInstallmentByLoansAndStatus(any(), eq(PaymentStatus.NOT_PAID)))
                .thenReturn(List.of(installment));

        loanService.sweepOverdueLoans();

        assertEquals(LoanStatus.OVERDUE, loan.getStatus());
        assertEquals(LoanStatus.OVERDUE, installment.getStatus());
        verify(loanRepository).save(loan);
        verify(loanInstallmentRepository).save(installment);
    }

    @Test
    void applyLateFeesToOverdueLoans_shouldApplyFeesCorrectly() {
        ProductFee lateFee = ProductFee.builder()
                .feeType(FeeType.LATE)
                .amount(5.0)
                .isPercentage(false)
                .triggerDaysAfterDue(1)
                .build();

        Product product = Product.builder().fees(List.of(lateFee)).build();

        Loan loan = new Loan();
        loan.setProduct(product);
        loan.setDueDate(new Date(System.currentTimeMillis() - 86400000 * 3));
        loan.setBalance(100.0);
        loan.setStatus(LoanStatus.OVERDUE);

        when(loanRepository.findLoanByStatus(LoanStatus.OVERDUE)).thenReturn(List.of(loan));

        loanService.applyLateFeesToOvedueLoans();

        assertEquals(105.0, loan.getBalance());
        verify(loanRepository).save(loan);
    }

    @Test
    void consolidateLoanDueDates_shouldUpdateInstallments() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus(LoanStatus.OPEN);
        loan.setDueDate(new Date());

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setDueDate(new Date());
        installment1.setPaymentStatus(PaymentStatus.NOT_PAID);

        LoanInstallment installment2 = new LoanInstallment();
        installment2.setDueDate(new Date());
        installment2.setPaymentStatus(PaymentStatus.NOT_PAID);

        Product product = Product.builder().name("Test").id(1L).build();
        loan.setProduct(product);

        when(loanRepository.findLoanByAccountNumberAndStatus(anyLong(), any()))
                .thenReturn(List.of(loan));
        when(loanInstallmentRepository.findLoanInstallmentByLoans(any()))
                .thenReturn(List.of(installment1, installment2));
        when(localizationService.getMessage(any(), isNull())).thenReturn("Consolidation successful.");

        LoanRequestDTO requestDTO = new LoanRequestDTO();
        requestDTO.setAccountNumber(123456L);
        requestDTO.setConsolidateDueDate(new Date().getTime());

        String result = loanService.consolidateLoanDueDates(requestDTO);
        assertEquals("Consolidation successful.", result);
    }

    @Test
    void sendDueDateReminders_shouldSendSmsToOpenLoans() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.OPEN);
        loan.setBalance(100.0);
        User user = new User();
        Account account = new Account();
        account.setCustomer(user);
        loan.setAccount(account);

        when(loanRepository.findLoanByStatusAndDate(eq(LoanStatus.OPEN), any()))
                .thenReturn(List.of(loan));

        loanService.sendDueDateReminders();
        verify(africasTalkingGateway).sentReminderMessages(List.of(user));
    }
}
