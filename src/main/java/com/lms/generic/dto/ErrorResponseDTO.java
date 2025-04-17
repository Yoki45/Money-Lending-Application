package com.lms.generic.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@Schema(name = "Error Response", description = "Schema holding the error response")
public class ErrorResponseDTO {

    @Schema(description = "API path invoked by client")
    private String apiPath;
    @Schema(description = "Error code of the error that happened")
    private HttpStatus errorCode;
    @Schema(description = "Error message of the error that happened")
    private String errorMessage;
    @Schema(description = "Time of the day the error happened")
    private LocalDateTime errorTime;

}
