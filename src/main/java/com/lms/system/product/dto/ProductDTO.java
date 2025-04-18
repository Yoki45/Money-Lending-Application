package com.lms.system.product.dto;

import com.lms.system.product.enums.TenureType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    @Schema(description = "Unique identifier of the product", example = "1")
    private Long id;

    @Schema(description = "Name of the loan product", example = "30-Day Quick Loan")
    private String name;

    @Schema(description = "Type of tenure: DAYS or MONTHS", example = "DAYS")
    private TenureType tenureType;

    @Schema(description = "Duration of the loan in selected tenure type", example = "30")
    private Integer tenureValue;

    @Schema(description = "List of fees associated with this product")
    private List<ProductFeeDTO> fees;
}
