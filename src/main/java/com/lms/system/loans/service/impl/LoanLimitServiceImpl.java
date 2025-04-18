package com.lms.system.loans.service.impl;

import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.user.model.User;
import com.lms.system.customer.user.repository.UserRepository;
import com.lms.system.loans.enums.LoanRiskCategory;
import com.lms.system.loans.model.CreditScore;
import com.lms.system.loans.model.LoanLimit;
import com.lms.system.loans.model.LoanLimitHistory;
import com.lms.system.loans.repository.*;
import com.lms.system.loans.service.ILoanLimitService;
import com.lms.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoanLimitServiceImpl implements ILoanLimitService {

    private  final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final CreditScoreRepository creditScoreRepository;

    private final LoanLimitRepository loanLimitRepository;

    private final LoanLimitHistoryRepository loanLimitHistoryRepository;


    @Override
    public void calculateLoanLimit() {
        List<User> users = userRepository.findAll();
        List<CreditScore> creditScores = creditScoreRepository.findCreditScoreByUsers(users);

        Map<User, Account> userAccountMap = accountRepository.findAccountByCustomers(
                        creditScores.stream().map(CreditScore::getCustomer).toList())
                .stream().collect(Collectors.toMap(Account::getCustomer, Function.identity()));

        Map<User, LoanLimit> loanLimitMap = loanLimitRepository.findLoanLimitsByUser(users)
                .stream().collect(Collectors.toMap(LoanLimit::getCustomer, Function.identity()));

        for (CreditScore creditScore : creditScores) {
            User user = creditScore.getCustomer();
            Account account = userAccountMap.get(user);
            if (account == null) continue;

            double score = creditScore.getScore();
            double deposits = account.getDeposits();

            double multiplier = Utils.MULTIPLIER_INELIGIBLE;
            LoanRiskCategory risk = LoanRiskCategory.INELIGIBLE;

            if (score >= 800) {
                multiplier = Utils.MULTIPLIER_LOW;
                risk = LoanRiskCategory.LOW;
            } else if (score >= 750) {
                multiplier = Utils.MULTIPLIER_MEDIUM;
                risk = LoanRiskCategory.MEDIUM;
            } else if (score >= 700) {
                multiplier = Utils.MULTIPLIER_ELEVATED;
                risk = LoanRiskCategory.ELEVATED;
            } else if (score >= 600) {
                multiplier = Utils.MULTIPLIER_HIGH;
                risk = LoanRiskCategory.HIGH;
            }

            double maxLoanAmount = deposits * multiplier;

            LoanLimit loanLimit = loanLimitMap.getOrDefault(user, new LoanLimit());
            loanLimit.setCustomer(user);
            loanLimit.setLimit(maxLoanAmount);
            loanLimit.setCategory(risk);
            loanLimitRepository.save(loanLimit);

            LoanLimitHistory history = LoanLimitHistory.builder()
                    .loanLimit(loanLimit)
                    .limit(maxLoanAmount)
                    .build();
            loanLimitHistoryRepository.save(history);

            log.info("User [{}] - Score: {}, Limit: {}, Risk: {}", user.getUsername(), score, maxLoanAmount, risk);
        }
    }


}
