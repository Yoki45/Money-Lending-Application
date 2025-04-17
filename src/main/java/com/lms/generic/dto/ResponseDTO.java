package com.lms.generic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "Response", description = "Holds successful response status")
public class ResponseDTO {
    @Schema(name = "Status code", example = "200")
    private String statusCode;
    @Schema(name = "Status message", example = "Response executed successfully")
    private String statusMsg;



}
