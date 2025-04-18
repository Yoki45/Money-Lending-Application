package com.lms.loan;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.user.model.User;
import com.lms.system.customer.user.repository.UserRepository;
import com.lms.system.loans.enums.LoanRiskCategory;
import com.lms.system.loans.model.CreditScore;
import com.lms.system.loans.model.LoanLimit;
import com.lms.system.loans.model.LoanLimitHistory;
import com.lms.system.loans.repository.*;
import com.lms.system.loans.service.impl.LoanLimitServiceImpl;
import com.lms.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;


@ExtendWith(MockitoExtension.class)
public class LoanLimitServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private CreditScoreRepository creditScoreRepository;
    @Mock private LoanLimitRepository loanLimitRepository;
    @Mock private LoanLimitHistoryRepository loanLimitHistoryRepository;

    @InjectMocks
    private LoanLimitServiceImpl loanLimitService;

    private User user;
    private Account account;
    private CreditScore creditScore;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("john.doe");

        account = new Account();
        account.setCustomer(user);
        account.setDeposits(10000.0); // base deposits

        creditScore = new CreditScore();
        creditScore.setCustomer(user);
        creditScore.setScore(800.0); // should fall in LOW
    }

    @Test
    void calculateLoanLimit_ShouldCalculateAndPersistLoanLimitCorrectly() {

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(creditScoreRepository.findCreditScoreByUsers(List.of(user))).thenReturn(List.of(creditScore));
        when(accountRepository.findAccountByCustomers(List.of(user))).thenReturn(List.of(account));
        when(loanLimitRepository.findLoanLimitsByUsers(List.of(user))).thenReturn(List.of());

        ArgumentCaptor<LoanLimit> loanLimitCaptor = ArgumentCaptor.forClass(LoanLimit.class);
        ArgumentCaptor<LoanLimitHistory> historyCaptor = ArgumentCaptor.forClass(LoanLimitHistory.class);

        loanLimitService.calculateLoanLimit();

        verify(loanLimitRepository).save(loanLimitCaptor.capture());
        LoanLimit savedLimit = loanLimitCaptor.getValue();
        assertEquals(user, savedLimit.getCustomer());
        assertEquals(LoanRiskCategory.LOW, savedLimit.getCategory());
        assertEquals(10000.0 * Utils.MULTIPLIER_LOW, savedLimit.getLimit());

        verify(loanLimitHistoryRepository).save(historyCaptor.capture());
        LoanLimitHistory history = historyCaptor.getValue();
        assertEquals(savedLimit, history.getLoanLimit());
        assertEquals(savedLimit.getLimit(), history.getLimit());
    }

    @Test
    void calculateLoanLimit_ShouldSkipIfNoAccount() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(creditScoreRepository.findCreditScoreByUsers(List.of(user))).thenReturn(List.of(creditScore));
        when(accountRepository.findAccountByCustomers(List.of(user))).thenReturn(List.of()); // No account

        loanLimitService.calculateLoanLimit();

        verify(loanLimitRepository, never()).save(any());
        verify(loanLimitHistoryRepository, never()).save(any());
    }




}
