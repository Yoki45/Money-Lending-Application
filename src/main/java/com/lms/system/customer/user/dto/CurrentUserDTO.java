package com.lms.system.customer.user.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "Current User Details", description = "Holds user login details")
public class CurrentUserDTO extends UserDTO {

    private String accessToken;

    private String refreshToken;

}
