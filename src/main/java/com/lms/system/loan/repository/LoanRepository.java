package com.lms.system.loan.repository;

import com.lms.generic.repository.GenericRepository;
import com.lms.system.customer.account.model.Account;
import com.lms.system.loan.enums.LoanStatus;
import com.lms.system.loan.model.Loan;

import java.util.Date;
import java.util.List;

public interface LoanRepository extends GenericRepository<Loan, Long> {


    List<Loan> findLoanByAccounts(List<Account> account);

    List<Loan> findLoanByAccountNumberAndProduct(Long accountNumber, Integer product, List<LoanStatus> statuses);

    Double totalLoansByAccountNumber(Long accountNumber, List<LoanStatus> statuses);

    Loan findLoanByLoanId(Long loanId);

    List<Loan> findLoanByStatusAndDate(LoanStatus status, Date loanDate);

    List<Loan> findLoanByStatus(LoanStatus status);

    List<Loan> getLoans(LoanStatus status, Long loanId,Date startDate, Date endDate, Long customer,Long product,Long accountNumber);

    List<Loan> findLoanByAccountNumberAndStatus(Long accountNumber, List<LoanStatus> statuses);

}
