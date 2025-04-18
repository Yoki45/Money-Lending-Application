package com.lms.system.loans.repository;

import com.lms.generic.repository.GenericRepository;
import com.lms.system.customer.account.model.Account;
import com.lms.system.loans.enums.LoanStatus;
import com.lms.system.loans.model.Loan;

import java.util.List;

public interface LoanRepository extends GenericRepository<Loan, Long> {


    List<Loan> findLoanByAccounts(List<Account> account);

    List<Loan> findLoanByAccountNumberAndProduct(Long accountNumber, Integer product, List<LoanStatus> statuses);

    Double totalLoansByAccountNumber(Long accountNumber, List<LoanStatus> statuses);
}
