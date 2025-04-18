package com.lms.system.loans.repository;

import com.lms.system.loans.model.LoanLimitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanLimitHistoryRepository extends JpaRepository<LoanLimitHistory, Long> {
}
