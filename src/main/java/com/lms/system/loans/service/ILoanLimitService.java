package com.lms.system.loans.service;

import com.lms.system.loans.dto.LoanLimitDTO;

import java.util.List;

public interface ILoanLimitService {

     void calculateLoanLimit();

     LoanLimitDTO getUserLoanLimit();

     List<LoanLimitDTO> getLoanLimitHistory(Long id);
}
