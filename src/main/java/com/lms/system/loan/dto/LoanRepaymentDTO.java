package com.lms.system.loan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class LoanRepaymentDTO {

    @Schema(description = "ID of the loan being repaid", example = "1001")
    private Long loanId;

    @Schema(description = "Amount being paid towards the loan", example = "1500.00")
    private Double amount;

    @Schema(description = "Date paid ", example = "1500.00")
    private Date date;

    public LoanRepaymentDTO(Long loanId, Double amount, Date date) {
        this.loanId = loanId;
        this.amount = amount;
        this.date = date;

    }

}
