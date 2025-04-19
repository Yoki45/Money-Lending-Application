package com.lms.system.loan.service;

import com.lms.system.loan.dto.LoanLimitDTO;

import java.util.List;

public interface ILoanLimitService {

     void calculateLoanLimit();

     LoanLimitDTO getUserLoanLimit();

     List<LoanLimitDTO> getLoanLimitHistory(Long id);
}
