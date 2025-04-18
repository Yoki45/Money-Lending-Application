package com.lms.system.loans.repository;

import com.lms.generic.repository.GenericRepositoryImpl;
import com.lms.system.customer.account.model.Account;
import com.lms.system.loans.model.Loan;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class LoanRepositoryImpl extends GenericRepositoryImpl<Loan,Long> implements LoanRepository {



    @Override
    public List<Loan> findLoanByAccounts(List<Account> account) {

        if(account == null || account.isEmpty()) return new ArrayList<>();

        Query query = em.createQuery("select l from Loan l where l.account IN :account");
        query.setParameter("account", account);

        return query.getResultList();
    }



    @Override
    public Loan findById(Long id) {
        return null;
    }

    @Override
    public Loan findById(Long id, String graphName) {
        return null;
    }

    @Override
    public Loan getReference(Long id) {
        return null;
    }

    @Override
    public void delete(Long[] ids)  {

    }

    @Override
    public List<Loan> findAll() {
        return List.of();
    }

    @Override
    public List<Loan> findAll(String fieldList) {
        return List.of();
    }

    @Override
    public List<Loan> findAll(String fieldList, List<String> criterion) {
        return List.of();
    }

    @Override
    public void deleteById(Long id)  {

    }


}
