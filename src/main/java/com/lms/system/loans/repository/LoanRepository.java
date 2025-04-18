package com.lms.system.loans.repository;

import com.lms.generic.repository.GenericRepository;
import com.lms.system.customer.account.model.Account;
import com.lms.system.loans.model.Loan;

import java.util.List;

public interface LoanRepository extends GenericRepository<Loan, Long> {


    List<Loan> findLoanByAccounts(List<Account> account);
}
