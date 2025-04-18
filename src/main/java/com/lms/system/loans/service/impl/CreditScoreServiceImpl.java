package com.lms.system.loans.service.impl;

import com.lms.generic.dateRange.model.DateRangeFilter;
import com.lms.generic.dateRange.service.DateFilterRangeService;
import com.lms.system.customer.account.enums.TransactionType;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.model.Transaction;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.account.repository.TransactionRepository;
import com.lms.system.customer.user.model.User;
import com.lms.system.customer.user.repository.UserRepository;
import com.lms.system.loans.enums.LoanStatus;
import com.lms.system.loans.model.CreditScore;
import com.lms.system.loans.model.CreditScoreHistory;
import com.lms.system.loans.model.Loan;
import com.lms.system.loans.model.LoanRepaymentHistory;
import com.lms.system.loans.repository.CreditScoreHistoryRepository;
import com.lms.system.loans.repository.CreditScoreRepository;
import com.lms.system.loans.repository.LoanRepaymentRepository;
import com.lms.system.loans.repository.LoanRepository;
import com.lms.system.loans.service.ICreditScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lms.utils.Utils.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreditScoreServiceImpl implements ICreditScoreService {

    private final LoanRepository loanRepository;

    private  final LoanRepaymentRepository loanRepaymentRepository;

    private  final TransactionRepository transactionRepository;

    private  final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private  final DateFilterRangeService dateFilterRangeService;

    private final CreditScoreRepository creditScoreRepository;

    private final CreditScoreHistoryRepository creditScoreHistoryRepository;


    @Override
    @Transactional
    public void calculateCreditScore() {
        DateRangeFilter rangeFilter = dateFilterRangeService.getFilterDateRange("this_month");

        List<User> users = userRepository.findAll();
        List<Account> accounts = accountRepository.findAccountByCustomers(users);
        List<Loan> loans = loanRepository.findLoanByAccounts(accounts);
        List<LoanRepaymentHistory> repaymentHistories = loanRepaymentRepository.findMonthlyRepaymentHistoryByLoans(loans);
        List<Transaction> transactions = transactionRepository.findMonthlyTransactions(
                accounts,
                rangeFilter.getBeginCalendar().getTime(),
                rangeFilter.getEndCalendar().getTime()
        );

        Map<User, CreditScore> creditScoresMap = creditScoreRepository.findCreditScoreByUsers(users)
                .stream().collect(Collectors.toMap(CreditScore::getCustomer, c -> c));

        Map<Account, List<Transaction>> accountTransactions = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getAccount));

        Map<Account, List<Loan>> accountLoans = loans.stream()
                .collect(Collectors.groupingBy(Loan::getAccount));

        Map<Loan, List<LoanRepaymentHistory>> loanRepayments = repaymentHistories.stream()
                .collect(Collectors.groupingBy(LoanRepaymentHistory::getLoan));

        for (User user : users) {
            double score = MINIMUM_CREDIT_SCORE;

            Account account = accounts.stream()
                    .filter(a -> a.getCustomer().getId().equals(user.getId()))
                    .findFirst()
                    .orElse(null);
            if (account == null) continue;

            List<Transaction> userTransactions = accountTransactions.getOrDefault(account, List.of());

            double monthlyDeposits = userTransactions.stream()
                    .filter(t -> t.getTransactionType().equals(TransactionType.DEPOSIT))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            if (monthlyDeposits > DEPOSIT_THRESHOLD_3) {
                score += score * DEPOSIT_BONUS_3_PERCENT;
            } else if (monthlyDeposits > DEPOSIT_THRESHOLD_2) {
                score += score * DEPOSIT_BONUS_2_PERCENT;
            } else if (monthlyDeposits > DEPOSIT_THRESHOLD_1) {
                score += score * DEPOSIT_BONUS_1_PERCENT;
            }

            long transactionCount = userTransactions.size();
            if (transactionCount > 20) {
                score += score * TRANSACTION_BONUS_2_PERCENT;
            } else if (transactionCount >= 10) {
                score += score * TRANSACTION_BONUS_1_PERCENT;
            }

            List<Loan> userLoans = accountLoans.getOrDefault(account, List.of());

            boolean hasOverdue = userLoans.stream()
                    .anyMatch(l -> l.getStatus().equals(LoanStatus.OVERDUE) && l.getRepaidDate() == null);
            if (hasOverdue) {
                score -= score * 0.05;
            }

            long onTime = 0;
            long late = 0;
            for (Loan loan : userLoans) {
                List<LoanRepaymentHistory> repayments = loanRepayments.getOrDefault(loan, List.of());
                onTime += repayments.stream().filter(LoanRepaymentHistory::getRepaidOnTime).count();
                late += repayments.stream().filter(r -> !r.getRepaidOnTime()).count();
            }

            score += score * (onTime * ON_TIME_REPAYMENT_BONUS_PERCENT);
            score -= score * (late * LATE_REPAYMENT_PENALTY_PERCENT);
            score = Math.max(MINIMUM_CREDIT_SCORE, Math.min(score, MAX_SCORE));

            CreditScore creditScore = creditScoresMap.getOrDefault(user, new CreditScore());
            creditScore.setCustomer(user);
            creditScore.setScore(score);
            creditScore = creditScoreRepository.save(creditScore);

            CreditScoreHistory creditScoreHistory = CreditScoreHistory.builder()
                    .creditScore(creditScore)
                    .score(score)
                    .build();
            creditScoreHistoryRepository.save(creditScoreHistory);

            log.info("User [{}] — Credit Score: {}", user.getUsername(), (int) score);
            log.debug("Deposits: {}, Txns: {}, On-time: {}, Late: {}", monthlyDeposits, transactionCount, onTime, late);
        }
    }


}
