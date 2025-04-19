package com.lms.system.loan.service;


import com.lms.system.loan.dto.LoanRepaymentDTO;

import java.util.List;

public interface ILoanRepaymentService {

    String repayLoan(LoanRepaymentDTO loanRepaymentDTO);

    List<LoanRepaymentDTO> getLoanRepayments(Long loanId);

}
