package com.lms.system.loans.repository;

import com.lms.system.loans.model.CreditScoreHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditScoreHistoryRepository extends JpaRepository<CreditScoreHistory, Long> {

    @Query("SELECT cr FROM CreditScoreHistory  cr WHERE  cr.creditScore.id = :creditScoreId")
    List<CreditScoreHistory> findByCreditScoreId(Long creditScoreId);
}
