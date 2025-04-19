package com.lms.system.loan.repository;

import com.lms.system.loan.enums.LoanStatus;
import com.lms.system.loan.enums.PaymentStatus;
import com.lms.system.loan.model.Loan;
import com.lms.system.loan.model.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {


    @Query("SELECT il FROM LoanInstallment il WHERE il.loan = :loan AND il.paymentStatus = :status ORDER BY il.dueDate asc ")
    List<LoanInstallment> findLoanInstallmentByLoanAndStatus(Loan loan, PaymentStatus status);

    @Query("SELECT COUNT(il) FROM LoanInstallment il WHERE il.loan.id = :loanId AND il.paymentStatus = :status")
    long countUnpaidInstallmentsByLoanId(@Param("loanId") Long loanId, PaymentStatus status);


    @Query("SELECT il FROM LoanInstallment il WHERE il.loan IN :loan AND il.paymentStatus = :status ORDER BY il.dueDate asc ")
    List<LoanInstallment> findLoanInstallmentByLoansAndStatus(List<Loan> loan, PaymentStatus status);

}
