package com.lms.system.loans.repository;

import com.lms.system.loans.model.CreditScoreHistory;
import com.lms.system.loans.model.LoanLimitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanLimitHistoryRepository extends JpaRepository<LoanLimitHistory, Long> {

    @Query("SELECT cr FROM LoanLimitHistory  cr WHERE  cr.loanLimit.id = :id")
    List<LoanLimitHistory> findLoanLimitHistoriesById(Long id);
}
