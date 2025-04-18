package com.lms.system.loans.repository;

import com.lms.system.customer.user.model.User;
import com.lms.system.loans.model.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditScoreRepository  extends JpaRepository<CreditScore, Long> {

    @Query("SELECT cr from CreditScore  cr where  cr.customer in :users")
    List<CreditScore> findCreditScoreByUsers(List<User> users);

}
