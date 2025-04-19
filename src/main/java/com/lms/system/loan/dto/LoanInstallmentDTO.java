package com.lms.system.loan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class LoanInstallmentDTO {


    @Schema(description = "Unique identifier of the loan installment", example = "1001")
    private Long installmentId;

    @Schema(description = "Sequential number of the installment in the loan schedule", example = "1")
    private Integer installmentNumber;

    @Schema(description = "Amount due for this specific installment", example = "2500.00")
    private Double amount;

    @Schema(description = "Due date for the installment payment", example = "2025-05-18")
    private Date dueDate;

}
