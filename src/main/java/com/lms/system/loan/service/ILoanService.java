package com.lms.system.loan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lms.system.loan.dto.LoanReportDTO;
import com.lms.system.loan.dto.LoanRequestDTO;
import com.lms.system.loan.enums.LoanStatus;

import java.util.Date;

public interface ILoanService {

    String requestForLoan(LoanRequestDTO loanRequestDTO) throws JsonProcessingException;

    void sweepOverdueLoans();

    void applyLateFeesToOvedueLoans();

    void sendDueDateReminders();

    LoanReportDTO fetchLoanDetails(LoanStatus status, Long loanId, String range, Long customer, Long product, Long accountNumber,Integer page);

    String consolidateLoanDueDates(LoanRequestDTO loanRequestDTO);



}
