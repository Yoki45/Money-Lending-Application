package com.lms.system.customer.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lms.system.customer.account.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Schema(name = "Transaction",description = "Holds transaction information of an account")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionsResponseDTO {

    @Schema(description = "Unique identifier of the transaction")
    private Long transactionId;
    @Schema(description = "The total amount made in the transaction")
    private Double amount;
    @Schema(description = "Type of Transaction")
    private TransactionType transactionType;
}
