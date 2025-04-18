package com.lms.system.loans.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lms.system.loans.dto.LoanRequestDTO;

public interface ILoanService {


    String requestForLoan(LoanRequestDTO loanRequestDTO) throws JsonProcessingException;
}
