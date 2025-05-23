package com.lms.system.customer.account.repository;

import com.lms.system.customer.account.enums.TransactionType;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t " +
            "WHERE (:accountNumber IS NULL OR t.account.accountNumber = :accountNumber) " +
            "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
            "AND (:startDate IS NULL OR t.createdOn >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdOn <= :endDate)")
    List<Transaction> findTransactions(Long accountNumber, TransactionType transactionType, Date startDate, Date endDate);


    @Query("SELECT t FROM Transaction t " +
            "WHERE t.account IN :accounts AND t.createdOn >= :startDate AND t.createdOn <= :endDate ")
    List<Transaction> findMonthlyTransactions(List<Account> accounts, Date startDate, Date endDate);


}
