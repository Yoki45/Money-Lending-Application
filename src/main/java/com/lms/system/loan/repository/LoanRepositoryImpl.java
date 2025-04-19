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
        if (status == null) return new ArrayList<>();

        Query query = em.createQuery("select l from Loan l where l.status = :status  ");
        query.setParameter("status", status);

        return query.getResultList();

    }

    @Override
    public List<Loan> getLoans(LoanStatus status, Long loanId, Date startDate, Date endDate, Long customer, Long product, Long accountNumber) {

        StringBuilder stringBuilder = new StringBuilder(" SELECT l from Loan l where 1=1 ");

        getLoansQuery(status, loanId, startDate, endDate, customer, product, accountNumber, stringBuilder);

        Query query = em.createQuery(stringBuilder.toString());

        setLoanParameters(status, loanId, startDate, endDate, customer, product, accountNumber, query);

        return query.getResultList();
    }

    @Override
    public List<Loan> findLoanByAccountNumberAndStatus(Long accountNumber, List<LoanStatus> statuses) {
        if (accountNumber == null ) return new ArrayList<>();

        if (statuses.isEmpty()) statuses = Arrays.asList(LoanStatus.OVERDUE);

        Query query = em.createQuery("select l from Loan l where l.account.accountNumber = :account AND l.status in :statuses");
        query.setParameter("account", accountNumber);
        query.setParameter("statuses", statuses);

        return query.getResultList();

    }

    private void getLoansQuery(LoanStatus status, Long loanId, Date startDate, Date endDate, Long customer, Long product, Long accountNumber, StringBuilder stringBuilder) {
        if (status != null) {
            stringBuilder.append(" and l.status = :status ");
        }

        if (loanId != null) {
            stringBuilder.append(" and l.id = :loanId ");
        }

        if (startDate != null) {
            stringBuilder.append(" and l.createdOn >= :startDate ");
        }

        if (endDate != null) {
            stringBuilder.append(" and l.createdOn <= :endDate ");
        }

        if (customer != null) {
            stringBuilder.append(" and l.account.customer.id  = :customer ");
        }

        if (product != null) {
            stringBuilder.append(" and l.product.id = :product ");
        }

        if (accountNumber != null) {
            stringBuilder.append(" and l.account.accountNumber = :accountNumber ");
        }
    }

    private void setLoanParameters(LoanStatus status, Long loanId, Date startDate, Date endDate, Long customer, Long product, Long accountNumber, Query query) {

        if (loanId != null) {
            query.setParameter("loanId", loanId);
        }

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (customer != null) {
            query.setParameter("customer", customer);
        }

        if (product != null) {
            query.setParameter("product", product);
        }

        if (accountNumber != null) {
            query.setParameter("accountNumber", accountNumber);
        }

        if (status != null) {
            query.setParameter("status", status);
        }
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
