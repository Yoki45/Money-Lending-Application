package com.lms.system.loan.repository;

import com.lms.system.loan.model.Loan;
import com.lms.system.loan.model.LoanRepaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepaymentRepository extends JpaRepository<LoanRepaymentHistory,Long> {

    @Query("SELECT lr FROM LoanRepaymentHistory lr where lr.loan in :loans")
    List<LoanRepaymentHistory> findRepaymentHistoryByLoans(List<Loan> loans);

}
