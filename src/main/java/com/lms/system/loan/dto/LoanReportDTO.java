package com.lms.system.loan.dto;

import com.lms.generic.dto.PageInfoDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoanReportDTO extends PageInfoDTO {

    private Double totalOverdueLoans;

    private Double totalClosedLoans;

    private Double balance;

    private List<LoanResponseDTO> loans;


    public LoanReportDTO(int currentPage, int totalPages, Double totalOverdueLoans, Double totalClosedLoans, List<LoanResponseDTO> loans, Double balance) {
        super(currentPage, totalPages);
        this.totalOverdueLoans = totalOverdueLoans;
        this.totalClosedLoans = totalClosedLoans;
        this.balance = balance;
        this.loans = loans;
    }
}
