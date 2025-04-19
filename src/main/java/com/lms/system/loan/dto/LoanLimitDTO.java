package com.lms.system.loan.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lms.system.loan.enums.LoanRiskCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "LoanLimitDTO",
        description = "Schema to hold Loan Limit Request Information"
)
public class LoanLimitDTO {

    @Schema(description = "Unique Identifier of Loan Limit")
    private  Long id;

    @Schema(description = "Limit amount of User")
    private  double limit;

    @Schema(description = "Last updated date")
    private Date lastUpdated;

    @Schema(description = "Loan Risk Category")
    private LoanRiskCategory category;

}
