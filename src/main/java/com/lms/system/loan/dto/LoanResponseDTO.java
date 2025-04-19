package com.lms.system.loan.dto;

import com.lms.system.loan.enums.LoanStatus;
import com.lms.system.loan.enums.LoanType;
import com.lms.system.loan.model.LoanInstallment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class LoanResponseDTO {

    @Schema(description = "Unique identifier for the loan", example = "12345")
    private Long id;

    @Schema(description = "Name of the product tied to this loan", example = "30-Day Quick Loan")
    private String productName;

    @Schema(description = "ID of the product tied to this loan", example = "1")
    private Long productId;

    @Schema(description = "Original loan amount", example = "10000.00")
    private Double amount;

    @Schema(description = "Remaining balance of the loan", example = "5000.00")
    private Double balance;

    @Schema(description = "Current status of the loan", example = "OPEN")
    private LoanStatus status;

    @Schema(description = "Due date of the loan", example = "2025-05-30")
    private Date dueDate;

    @Schema(description = "List of loan installments for this loan")
    private List<LoanInstallmentDTO> installments;

    @Schema(description = "Loan Type ", example = "OPEN")
    private LoanType loanType;


}
