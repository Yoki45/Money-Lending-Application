package com.lms.system.customer.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "AccountRequestDTO",
        description = "Schema to hold Account Request Information"
)
public class AccountRequestDTO extends AccountsDTO {

    @Schema(description = "Deposited or withdrawal amount")
    private Double amount;
}
