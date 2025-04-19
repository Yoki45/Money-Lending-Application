package com.lms.system.loan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {

    @Schema(description = "Unique account number tied to the loan", example = "1234567890", required = true)
    private Long accountNumber;

    @Schema(description = "ID of the loan product the customer is applying for", example = "3", required = true)
    private Integer productId;

    @Schema(description = "Requested loan amount", example = "5000.0", required = true)
    private Double amount;

    @Schema(description = "Requested number of installments for repayment", example = "3", required = true)
    private Integer numberOfInstallments;

    @Schema(description = "Optional loan reference for tracking the application", example = "LOAN-REQ-9832")
    private String reference;



}
