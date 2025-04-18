package com.lms.system.loans.repository;

import com.lms.system.loans.model.Loan;
import com.lms.system.loans.model.LoanRepaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepaymentRepository extends JpaRepository<LoanRepaymentHistory,Long> {

    @Query("SELECT lr FROM LoanRepaymentHistory lr where lr.loan in : loans")
    List<LoanRepaymentHistory> findMonthlyRepaymentHistoryByLoans(List<Loan> loans);

}
