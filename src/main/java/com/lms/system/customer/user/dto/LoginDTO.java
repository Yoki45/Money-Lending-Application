package com.lms.system.customer.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "Login", description = "Holds user login details")
public class LoginDTO {

    @Schema(description = "Email of the user", example = "hjfh@gmail.com")
    @NotEmpty(message = "Username is required")
    private String username;

    @Schema(description = "Paasword of the user", example = "****")
    @NotEmpty(message = "Password is required")
    private String password;
}
