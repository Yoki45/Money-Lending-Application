package com.lms.system.loan.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lms.generic.dto.ErrorResponseDTO;
import com.lms.generic.dto.ResponseDTO;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.loan.dto.LoanLimitDTO;
import com.lms.system.loan.dto.LoanReportDTO;
import com.lms.system.loan.dto.LoanRequestDTO;
import com.lms.system.loan.enums.LoanStatus;
import com.lms.system.loan.enums.LoanType;
import com.lms.system.loan.model.Loan;
import com.lms.system.loan.service.ILoanService;
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
@RequestMapping(path = "/loan", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "CRUD APIs for Loan module in Lms",
        description = "CREATE, READ , FETCH and DELETE  accounts")
@RequiredArgsConstructor
public class LoanController {

    private final ILoanService loanService;

    private final ILocalizationService localizationService;

    @Operation(summary = "Request for loan ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @PutMapping("request")
    public ResponseEntity<ResponseDTO> requestLoan(@Valid @RequestBody LoanRequestDTO loanRequestDTO) throws JsonProcessingException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(Utils.STATUS_200, loanService.requestForLoan(loanRequestDTO)));

    }


    @Operation(summary = "apply late fees manually")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @PutMapping("late-fee")
    public ResponseEntity<ResponseDTO> applyLateFeeManually() {
        loanService.applyLateFeesToOvedueLoans();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(Utils.STATUS_200, localizationService.getMessage("message.200.ok", null)));

    }


    @Operation(summary = "sent Loan Reminders manually")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @PutMapping("reminder")
    public ResponseEntity<ResponseDTO> sendReminderMessagesManually() {
        loanService.sendDueDateReminders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(Utils.STATUS_200, localizationService.getMessage("message.200.ok", null)));

    }


    @Operation(summary = "sent Loan Reminders manually")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @PutMapping("update-overdue")
    public ResponseEntity<ResponseDTO> sweepOverdueLoans() {
        loanService.sweepOverdueLoans();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(Utils.STATUS_200, localizationService.getMessage("message.200.ok", null)));

    }

    @Operation(summary = "Consolidate open loan due dates")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @PutMapping("consolidate")
    public ResponseEntity<ResponseDTO> consolidateOpenLoans(@Valid @RequestBody LoanRequestDTO loanRequestDTO) throws JsonProcessingException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(Utils.STATUS_200, loanService.consolidateLoanDueDates(loanRequestDTO)));

    }


    @Operation(summary = "Fetch loan details REST API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )


    @GetMapping()
    public ResponseEntity<LoanReportDTO> fetchLoanDetails(@RequestParam(required = false) LoanStatus status,
                                                          @RequestParam(required = false) Long loan, @RequestParam(required = false) String range,
                                                          @RequestParam(required = false) Long customer, @RequestParam(required = false) Long product, @RequestParam(required = false) Long accountNumber,
                                                          @RequestParam(required = false) Integer page, @RequestParam(required = false) LoanType type) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loanService.fetchLoanDetails(status, loan, range, customer, product, accountNumber, page, type));

    }


}
