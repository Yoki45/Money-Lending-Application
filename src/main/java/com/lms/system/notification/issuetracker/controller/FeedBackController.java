package com.lms.system.notification.issuetracker.controller;

import com.lms.generic.dto.ErrorResponseDTO;
import com.lms.generic.dto.ResponseDTO;
import com.lms.system.notification.issuetracker.dto.FeedBackRequestDTO;
import com.lms.system.notification.issuetracker.dto.FeedBackResponseDTO;
import com.lms.system.notification.issuetracker.service.IFeedBackService;
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
@RequestMapping(path = "/feed-back", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "CRUD APIs for FeedBack module in Lms",
        description = "CREATE, READ , FETCH and DELETE  accounts")
@RequiredArgsConstructor
public class FeedBackController {

    private final IFeedBackService feedBackService;


    @Operation(summary = "Fetch Feed Back details REST API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )


    @GetMapping()
    public ResponseEntity<List<FeedBackResponseDTO>> fetchFeedBackDetails() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(feedBackService.getAllFeedback());

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


    @PutMapping("resolve")
    public ResponseEntity<ResponseDTO> resolveFeedBack(@RequestParam Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO(Utils.STATUS_200, feedBackService.resolveFeedback(id)));

    }

    @Operation(summary = "Submit new feedBack")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @PostMapping()
    public ResponseEntity<ResponseDTO> submitFeedBack(@Valid @RequestBody FeedBackRequestDTO feedBackRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDTO(Utils.STATUS_201, feedBackService.submitFeedback(feedBackRequestDTO)));
    }


}
