package com.lms.system.loans.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "CreditScoreDTO",
        description = "Schema to hold Credit Score Request Information"
)
public class CreditScoreDTO {

    @Schema(description = "Unique Identifier of Credit Note")
    private  Long id;

    @Schema(description = "Credit Score of User")
    private  double score;



}
