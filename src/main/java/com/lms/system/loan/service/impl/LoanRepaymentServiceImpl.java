package com.lms.system.loan.service.impl;

import com.lms.generic.exception.BadRequestException;
import com.lms.generic.exception.NotFoundException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.loan.dto.LoanRepaymentDTO;
import com.lms.system.loan.enums.LoanStatus;
import com.lms.system.loan.enums.PaymentStatus;
import com.lms.system.loan.model.Loan;
import com.lms.system.loan.model.LoanInstallment;
import com.lms.system.loan.model.LoanRepaymentHistory;
import com.lms.system.loan.repository.LoanInstallmentRepository;
import com.lms.system.loan.repository.LoanRepaymentRepository;
import com.lms.system.loan.repository.LoanRepository;
import com.lms.system.loan.service.ILoanRepaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanRepaymentServiceImpl implements ILoanRepaymentService {


    private final LoanRepaymentRepository loanRepaymentRepository;

    private final LoanRepository loanRepository;

    private final LoanInstallmentRepository loanInstallmentRepository;

    private final ILocalizationService localizationService;


    @Override
    @Transactional
    public String repayLoan(LoanRepaymentDTO loanRepaymentDTO) {
        validateRepaymentRequest(loanRepaymentDTO);

        Loan loan = getLoanAndValidateStatus(loanRepaymentDTO.getLoanId());

        double remainingAmount = loanRepaymentDTO.getAmount();

        List<LoanInstallment> installments = loanInstallmentRepository.findLoanInstallmentByLoanAndStatus(
                loan, PaymentStatus.NOT_PAID);

        if (!installments.isEmpty()) {
            processInstallmentPayments(loan, remainingAmount,installments);

        } else {
            processLoanBalance(loan, remainingAmount);

        }

        loanRepository.save(loan);

        return localizationService.getMessage("message.200.ok", null);
    }

    @Override
    public List<LoanRepaymentDTO> getLoanRepayments(Long loanId) {
        if (loanId == null) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails", null));
        }

        Loan loan = Optional.ofNullable(loanRepository.findLoanByLoanId(loanId))
                .orElseThrow(() -> new NotFoundException(localizationService.getMessage("message.loan.notFound", null)));

        List<LoanRepaymentHistory> repaymentHistories = loanRepaymentRepository.findRepaymentHistoryByLoans(Collections.singletonList(loan));

        return repaymentHistories.stream()
                .map(history -> new LoanRepaymentDTO(loan.getId(),
                        history.getAmount(), history.getCreatedOn()
                ))
                .collect(Collectors.toList());
    }


    private void validateRepaymentRequest(LoanRepaymentDTO loanRepaymentDTO) {
        if (loanRepaymentDTO == null || loanRepaymentDTO.getAmount() <= 0) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails", null));
        }
    }

    private Loan getLoanAndValidateStatus(Long loanId) {
        Loan loan = loanRepository.findLoanByLoanId(loanId);
        if (loan == null) {
            throw new NotFoundException(localizationService.getMessage("message.loan.notFound", null));
        }
        if (loan.getStatus() == LoanStatus.CLOSED || loan.getStatus() == LoanStatus.WRITTEN_OFF) {
            throw new BadRequestException(localizationService.getMessage("message.loan.alreadyClosed", null));
        }
        return loan;
    }

    private double processInstallmentPayments(Loan loan, double remainingAmount,List<LoanInstallment> installments) {
        if (remainingAmount <= 0) {
            return remainingAmount;
        }

        for (LoanInstallment installment : installments) {
            if (remainingAmount <= 0) break;

            double balance = installment.getBalance();
            double paymentAmount = Math.min(remainingAmount, balance);

            updateInstallment(installment, balance, paymentAmount);
            createAndSaveRepaymentHistory(loan, paymentAmount);

            Double newloanBalance = loan.getBalance() - paymentAmount;

            loan.setBalance(newloanBalance);

            loanRepository.save(loan);

            remainingAmount -= paymentAmount;
            loanInstallmentRepository.save(installment);
        }

        checkAndUpdateLoanStatus(loan);

        return remainingAmount;
    }

    private void updateInstallment(LoanInstallment installment, double balance, double paymentAmount) {
        double newBalance = balance - paymentAmount;
        installment.setBalance(newBalance);

        if (newBalance == 0) {
            installment.setPaymentStatus(PaymentStatus.PAID);
        }
    }

    private void createAndSaveRepaymentHistory(Loan loan, double amount) {
        LoanRepaymentHistory repaymentHistory = new LoanRepaymentHistory();
        repaymentHistory.setLoan(loan);
        repaymentHistory.setAmount(amount);
        repaymentHistory.setStatus(loan.getStatus());
        repaymentHistory.setRepaidOnTime(!LoanStatus.OVERDUE.equals(loan.getStatus()));

        loanRepaymentRepository.save(repaymentHistory);
    }


    private void processLoanBalance(Loan loan, double remainingAmount) {
        double currentBalance = loan.getBalance();
        double newBalance = Math.max(0, currentBalance - remainingAmount);
        loan.setBalance(newBalance);

        if (newBalance == 0) {
            loan.setStatus(LoanStatus.CLOSED);
            loan.setRepaidDate(new Date());
        }

        this.createAndSaveRepaymentHistory(loan, remainingAmount);
    }

    private void checkAndUpdateLoanStatus(Loan loan) {
        if (loanInstallmentRepository.countUnpaidInstallmentsByLoanId(loan.getId(),PaymentStatus.NOT_PAID) == 0) {
            loan.setBalance(0.0);
            loan.setStatus(LoanStatus.CLOSED);
            loan.setRepaidDate(new Date());
        }
    }

}
