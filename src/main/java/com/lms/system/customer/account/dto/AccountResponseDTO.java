package com.lms.system.customer.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(
        name = "AccountResponseDTO",
        description = "Schema to hold Account Response Information"
)
public class AccountResponseDTO extends AccountsDTO {

    @Schema(description = "Total balance in a specific account")
    private  double balance;
    @Schema(description = "Total deposits in a specific account")
    private  double deposit;
    @Schema(description = "Total withdrawals in a specific account")
    private  double withdrawal;


}
