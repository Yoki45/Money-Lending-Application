package com.lms.system.loan.controller;

import com.lms.generic.dto.ErrorResponseDTO;
import com.lms.generic.dto.ResponseDTO;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.loan.dto.LoanLimitDTO;
import com.lms.system.loan.service.ILoanLimitService;
import com.lms.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/loan-limit", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "CRUD APIs for Loan Limit module in Lms",
        description = "CREATE, READ , FETCH and DELETE  accounts")
@RequiredArgsConstructor
public class LoanLimitController {


    private final ILoanLimitService loanLimitService;

    private final ILocalizationService localizationService;


    @Operation(summary = "update loan limit manually")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @PutMapping()
    public ResponseEntity<ResponseDTO> updateLoanLimitManually() {
        loanLimitService.calculateLoanLimit();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(Utils.STATUS_200, localizationService.getMessage("message.200.ok", null)));

    }


    @Operation(summary = "Fetch current loan limit details REST API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )


    @GetMapping()
    public ResponseEntity<LoanLimitDTO> fetchUserLoanLimit() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loanLimitService.getUserLoanLimit());

    }


    @Operation(summary = "Fetch user Loan limit history details REST API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )


    @GetMapping("history")
    public ResponseEntity<List<LoanLimitDTO>> fetchLoanLimitHistory(@RequestParam Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loanLimitService.getLoanLimitHistory(id));

    }


}
