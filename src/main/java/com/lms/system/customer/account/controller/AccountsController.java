package com.lms.system.customer.account.controller;

import com.lms.generic.dto.ErrorResponseDTO;
import com.lms.generic.dto.ResponseDTO;
import com.lms.system.customer.account.dto.AccountRequestDTO;
import com.lms.system.customer.account.dto.AccountsReportDTO;
import com.lms.system.customer.account.dto.TransactionReportDTO;
import com.lms.system.customer.account.enums.AccountType;
import com.lms.system.customer.account.enums.TransactionType;
import com.lms.system.customer.account.service.IAccountService;
import com.lms.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "CRUD APIs for Accounts module in Lms",
        description = "CREATE, READ , FETCH and DELETE  accounts")
@RequiredArgsConstructor
public class AccountsController {

    private final IAccountService accountService;


    @Operation(summary = "Deposit amount into an account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @PutMapping(value = "deposit")
    public ResponseEntity<ResponseDTO> depositAmount(@Valid @RequestBody AccountRequestDTO accountRequestDTO) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(Utils.STATUS_200, accountService.depositToAccount(accountRequestDTO)));

    }


    @Operation(summary = "WithDraw amount from an account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @PutMapping(value = "withdraw")
    public ResponseEntity<ResponseDTO> withdrawAmount(@Valid @RequestBody AccountRequestDTO accountRequestDTO) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(Utils.STATUS_200, accountService.withdrawFromAccount(accountRequestDTO)));

    }


    @Operation(summary = "Fetch Account details REST API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )


    @GetMapping()
    public ResponseEntity<AccountsReportDTO> fetchAgents(@RequestParam(required = false) Long customerId, @RequestParam(required = false) AccountType type,
                                                         @RequestParam(required = false, defaultValue = "1") Integer page) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountService.getAccountDetails(customerId, type, page));

    }


    @Operation(summary = "Fetch previous transactions details REST API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )


    @GetMapping("transactions")
    public ResponseEntity<TransactionReportDTO> fetchAgents(@RequestParam(required = false) Long accountNumber, @RequestParam(required = false) TransactionType type,
                                                            @RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false) String range) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountService.getTransactionReport(accountNumber, type, range, page));

    }


}
