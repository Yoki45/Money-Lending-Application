package com.lms.system.loan.controller;


import com.lms.generic.dto.ErrorResponseDTO;
import com.lms.generic.dto.ResponseDTO;
import com.lms.system.loan.dto.LoanLimitDTO;
import com.lms.system.loan.dto.LoanRepaymentDTO;
import com.lms.system.loan.service.ILoanRepaymentService;
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

import java.util.List;

@RestController
@RequestMapping(path = "/loan-repayment", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "CRUD APIs for Loan repayment module in Lms")
@RequiredArgsConstructor
public class LoanRepaymentController {

    private final ILoanRepaymentService loanRepaymentService;

    @Operation(summary = "Loan repayment API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @PutMapping()
    public ResponseEntity<ResponseDTO> repayLoan(@Valid @RequestBody LoanRepaymentDTO loanRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(Utils.STATUS_200, loanRepaymentService.repayLoan(loanRequestDTO)));

    }

    @Operation(summary = "Fetch loan repayment history details REST API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )


    @GetMapping("history")
    public ResponseEntity<List<LoanRepaymentDTO>> fetchLoanLimitHistory(@RequestParam Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loanRepaymentService.getLoanRepayments(id));

    }

}
