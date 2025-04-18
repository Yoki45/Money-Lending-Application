package com.lms.system.loans.repository;

import com.lms.system.customer.user.model.User;
import com.lms.system.loans.model.LoanLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanLimitRepository extends JpaRepository<LoanLimit, Long> {

    @Query("SELECT lm FROM LoanLimit lm WHERE lm.customer IN :users")
    List<LoanLimit> findLoanLimitsByUsers(List<User> users);

    @Query("SELECT cr from LoanLimit  cr where  cr.customer = :user")
    LoanLimit findLoanLimitByUser(User user);
}
