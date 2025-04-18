package com.lms.system.product.dto;

import com.lms.system.product.enums.FeeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFeeDTO {

    @Schema(description = "Type of the fee", example = "LATE")
    private FeeType feeType;

    @Schema(description = "Fee amount. Can be fixed or percentage", example = "100.0")
    private Double amount;

    @Schema(description = "True if the fee is percentage-based, false if it's fixed", example = "false")
    private Boolean isPercentage;

    @Schema(description = "Apply fee at disbursement (true) or not (false)", example = "true")
    private Boolean applyOnDisbursement;

    @Schema(description = "Days after due date when the fee should be applied (only for LATE fees)", example = "3")
    private Integer triggerDaysAfterDue;
}
