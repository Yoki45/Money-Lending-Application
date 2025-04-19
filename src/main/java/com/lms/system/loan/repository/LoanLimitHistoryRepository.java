package com.lms.system.loan.repository;

import com.lms.system.loan.model.LoanLimitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanLimitHistoryRepository extends JpaRepository<LoanLimitHistory, Long> {

    @Query("SELECT cr FROM LoanLimitHistory  cr WHERE  cr.loanLimit.id = :id")
    List<LoanLimitHistory> findLoanLimitHistoriesById(Long id);
}
