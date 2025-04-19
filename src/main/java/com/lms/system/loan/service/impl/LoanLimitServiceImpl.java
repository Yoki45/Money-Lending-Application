package com.lms.system.loan.service.impl;

import com.lms.generic.audit.AuditAwareImpl;
import com.lms.generic.exception.BadRequestException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.user.model.User;
import com.lms.system.customer.user.repository.UserRepository;
import com.lms.system.loan.dto.LoanLimitDTO;
import com.lms.system.loan.enums.LoanRiskCategory;
import com.lms.system.loan.model.CreditScore;
import com.lms.system.loan.model.LoanLimit;
import com.lms.system.loan.model.LoanLimitHistory;
import com.lms.system.loan.repository.*;
import com.lms.system.loan.service.ILoanLimitService;
import com.lms.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanLimitServiceImpl implements ILoanLimitService {

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final CreditScoreRepository creditScoreRepository;

    private final LoanLimitRepository loanLimitRepository;

    private final LoanLimitHistoryRepository loanLimitHistoryRepository;

    private final AuditAwareImpl auditAware;

    private final ILocalizationService localizationService;


    @Override
    public void calculateLoanLimit() {
        List<User> users = userRepository.findAll();
        List<CreditScore> creditScores = creditScoreRepository.findCreditScoreByUsers(users);

        Map<User, Account> userAccountMap = accountRepository.findAccountByCustomers(
                        creditScores.stream().map(CreditScore::getCustomer).toList())
                .stream().collect(Collectors.toMap(Account::getCustomer, Function.identity()));

        Map<User, LoanLimit> loanLimitMap = loanLimitRepository.findLoanLimitsByUsers(users)
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
            loanLimit = loanLimitRepository.save(loanLimit);

            LoanLimitHistory loanLimitHistory = new LoanLimitHistory();
            loanLimitHistory.setLoanLimit(loanLimit);
            loanLimitHistory.setLimit(maxLoanAmount);
            loanLimitHistory.setCategory(risk);

            loanLimitHistoryRepository.save(loanLimitHistory);

            log.info("User [{}] - Score: {}, Limit: {}, Risk: {}", user.getUsername(), score, maxLoanAmount, risk);
        }
    }

    @Override
    public LoanLimitDTO getUserLoanLimit() {
        User currentUser = auditAware.getCurrentLoggedInUser();
        LoanLimit loanLimit = loanLimitRepository.findLoanLimitByUser(currentUser);

        if (loanLimit == null) {
            return new LoanLimitDTO();
        }

        LoanLimitDTO limitDTO = new LoanLimitDTO();
        limitDTO.setId(loanLimit.getId());
        limitDTO.setLimit(loanLimit.getLimit());
        limitDTO.setCategory(loanLimit.getCategory());

        return limitDTO;


    }

    @Override
    public List<LoanLimitDTO> getLoanLimitHistory(Long id) {
        if (id == null) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails", null));
        }

        List<LoanLimitHistory> histories = loanLimitHistoryRepository.findLoanLimitHistoriesById(id);
        if (histories == null || histories.isEmpty()) {
            return Collections.emptyList();
        }

        return histories.stream()
                .map(history -> {
                    LoanLimitDTO limitDTO = new LoanLimitDTO();
                    limitDTO.setId(history.getId());
                    limitDTO.setLimit(history.getLimit());
                    limitDTO.setCategory(history.getCategory());
                    return limitDTO;
                })
                .collect(Collectors.toList());
    }


}
