package com.lms.system.loan.controller;

import com.lms.generic.dto.ErrorResponseDTO;
import com.lms.generic.dto.ResponseDTO;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.loan.dto.CreditScoreDTO;
import com.lms.system.loan.service.ICreditScoreService;
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
@RequestMapping(path = "/credit-score", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "CRUD APIs for Credit Score module in Lms",
        description = "CREATE, READ , FETCH and DELETE  accounts")
@RequiredArgsConstructor
public class CreditScoreController {

    private final ICreditScoreService creditScoreService;

    private final ILocalizationService localizationService;


    @Operation(summary = "update credit score manually")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @PutMapping()
    public ResponseEntity<ResponseDTO> updateCreditScoreManually() {
        creditScoreService.calculateCreditScore();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(Utils.STATUS_200, localizationService.getMessage("message.200.ok", null)));

    }


    @Operation(summary = "Fetch user credit score details REST API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )


    @GetMapping()
    public ResponseEntity<CreditScoreDTO> fetchUserCreditScore() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(creditScoreService.getUserCreditScore());

    }


    @Operation(summary = "Fetch user credit score History details REST API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )


    @GetMapping("history")
    public ResponseEntity<List<CreditScoreDTO>> fetchUserCreditScoreHistory(@RequestParam Long creditScoreId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(creditScoreService.getCreditScoreHistory(creditScoreId));

    }

}
