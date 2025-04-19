package com.lms.system.loan.repository;

import com.lms.generic.repository.GenericRepositoryImpl;
import com.lms.system.customer.account.model.Account;
import com.lms.system.loan.enums.LoanStatus;
import com.lms.system.loan.model.Loan;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class LoanRepositoryImpl extends GenericRepositoryImpl<Loan, Long> implements LoanRepository {


    @Override
    public List<Loan> findLoanByAccounts(List<Account> account) {

        if (account == null || account.isEmpty()) return new ArrayList<>();

        Query query = em.createQuery("select l from Loan l where l.account IN :account");
        query.setParameter("account", account);

        return query.getResultList();
    }

    @Override
    public List<Loan> findLoanByAccountNumberAndProduct(Long accountNumber, Integer product, List<LoanStatus> statuses) {

        if (accountNumber == null && product == null) return new ArrayList<>();

        if (statuses.isEmpty()) statuses = Arrays.asList(LoanStatus.OVERDUE);

        Query query = em.createQuery("select l from Loan l where l.account.accountNumber = :account AND l.product.id = :product AND l.status in :statuses");
        query.setParameter("account", accountNumber);
        query.setParameter("product", product);
        query.setParameter("statuses", statuses);

        return query.getResultList();

    }

    @Override
    public Double totalLoansByAccountNumber(Long accountNumber, List<LoanStatus> statuses) {

        if (accountNumber == null) {
            return 0d;
        }

        if (statuses == null || statuses.isEmpty()) {
            statuses = Arrays.asList(LoanStatus.OVERDUE, LoanStatus.OPEN);
        }

        Query query = em.createQuery("select SUM(COALESCE(l.amount, 0)) from Loan l where l.account.accountNumber = :accountNumber AND l.status in :statuses");
        query.setParameter("accountNumber", accountNumber);
        query.setParameter("statuses", statuses);

        Double result = (Double) query.getSingleResult();
        return result != null ? result : 0d;
    }

    @Override
    public Loan findLoanByLoanId(Long loanId) {

        if (loanId == null) {
            return null;
        }

        Query query = em.createQuery("select l from Loan l where l.id = :loanId ");
        query.setParameter("loanId", loanId);

        return (Loan) query.getSingleResult();
    }

    @Override
    public List<Loan> findLoanByStatusAndDate(LoanStatus status, Date loanDate) {

        if (status == null || loanDate == null) return new ArrayList<>();


        Query query = em.createQuery("select l from Loan l where l.status = :status and l.dueDate < :loanDate ");
        query.setParameter("status", status);
        query.setParameter("loanDate", loanDate);

        return query.getResultList();
    }

    @Override
    public List<Loan> findLoanByStatus(LoanStatus status) {
        if (status == null ) return new ArrayList<>();

        Query query = em.createQuery("select l from Loan l where l.status = :status  ");
        query.setParameter("status", status);

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
    public void delete(Long[] ids) {

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
    public void deleteById(Long id) {

    }


}
