package com.lms.system.customer.account.repository;

import com.lms.system.customer.account.enums.AccountType;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT c FROM Account c " +
            "WHERE (:customer IS NULL OR c.customer.id = :customer) " +
            "AND (:accountType IS NULL OR c.accountType = :accountType) ")
    List<Account> findAccounts(Long customer, AccountType accountType);


    Account findAccountByAccountNumber(Long accountNumber);

    @Query("SELECT u FROM Account  u WHERE  u.customer IN :users")
    List<Account> findAccountByCustomers(List<User> users);


}
