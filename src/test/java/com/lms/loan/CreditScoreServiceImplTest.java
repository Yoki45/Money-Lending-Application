package com.lms.loan;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.lms.utils.Utils.MINIMUM_CREDIT_SCORE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lms.generic.dateRange.model.DateRangeFilter;
import com.lms.generic.dateRange.service.DateFilterRangeService;
import com.lms.system.customer.account.enums.TransactionType;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.model.Transaction;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.account.repository.TransactionRepository;
import com.lms.system.customer.user.model.User;
import com.lms.system.customer.user.repository.UserRepository;
import com.lms.system.loan.enums.LoanStatus;
import com.lms.system.loan.model.*;
import com.lms.system.loan.repository.*;
import com.lms.system.loan.service.impl.CreditScoreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Calendar;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CreditScoreServiceImplTest {

    @Mock private LoanRepository loanRepository;
    @Mock private LoanRepaymentRepository loanRepaymentRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private DateFilterRangeService dateFilterRangeService;
    @Mock private CreditScoreRepository creditScoreRepository;
    @Mock private CreditScoreHistoryRepository creditScoreHistoryRepository;

    @InjectMocks
    private CreditScoreServiceImpl creditScoreService;

    private User user;
    private Account account;
    private Loan loan;
    private LoanRepaymentHistory repaymentOnTime;
    private Transaction transaction;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("jane.doe");

        account = new Account();
        account.setCustomer(user);
        account.setBalance(10000.0);

        loan = new Loan();
        loan.setAccount(account);
        loan.setStatus(LoanStatus.CLOSED);
        loan.setRepaidDate(null);

        repaymentOnTime = new LoanRepaymentHistory();
        repaymentOnTime.setLoan(loan);
        repaymentOnTime.setRepaidOnTime(true);

        transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(5000.0);
        transaction.setTransactionType(TransactionType.DEPOSIT);
    }

    @Test
    void calculateCreditScore_ShouldCalculateAndPersistScoreSuccessfully() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(accountRepository.findAccountByCustomers(List.of(user))).thenReturn(List.of(account));
        when(loanRepository.findLoanByAccounts(List.of(account))).thenReturn(List.of(loan));
        when(loanRepaymentRepository.findRepaymentHistoryByLoans(List.of(loan)))
                .thenReturn(List.of(repaymentOnTime));
        when(transactionRepository.findMonthlyTransactions(eq(List.of(account)), any(), any()))
                .thenReturn(List.of(transaction));
        when(dateFilterRangeService.getFilterDateRange("this_month"))
                .thenReturn(new DateRangeFilter(Calendar.getInstance(), Calendar.getInstance(), false));
        when(creditScoreRepository.findCreditScoreByUsers(List.of(user))).thenReturn(List.of());

        when(creditScoreRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<CreditScore> scoreCaptor = ArgumentCaptor.forClass(CreditScore.class);
        ArgumentCaptor<CreditScoreHistory> historyCaptor = ArgumentCaptor.forClass(CreditScoreHistory.class);

        creditScoreService.calculateCreditScore();

        verify(creditScoreRepository).save(scoreCaptor.capture());
        CreditScore savedScore = scoreCaptor.getValue();
        assertEquals(user, savedScore.getCustomer());
        assertTrue(savedScore.getScore() >= MINIMUM_CREDIT_SCORE);

        verify(creditScoreHistoryRepository).save(historyCaptor.capture());
        assertEquals(savedScore, historyCaptor.getValue().getCreditScore());
    }


    @Test
    void calculateCreditScore_ShouldSkipUserWithoutAccount() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(accountRepository.findAccountByCustomers(List.of(user))).thenReturn(List.of());
        when(creditScoreRepository.findCreditScoreByUsers(List.of(user))).thenReturn(List.of());

        when(dateFilterRangeService.getFilterDateRange("this_month"))
                .thenReturn(new DateRangeFilter(Calendar.getInstance(), Calendar.getInstance(), false));

        creditScoreService.calculateCreditScore();

        verify(creditScoreRepository, never()).save(any());
        verify(creditScoreHistoryRepository, never()).save(any());
    }


}
