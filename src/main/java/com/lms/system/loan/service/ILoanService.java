package com.lms.system.loan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lms.system.loan.dto.LoanRequestDTO;

public interface ILoanService {

    String requestForLoan(LoanRequestDTO loanRequestDTO) throws JsonProcessingException;

    void sweepOverdueLoans();

    void applyLateFeesToOvedueLoans();

    void sendDueDateReminders();





}
