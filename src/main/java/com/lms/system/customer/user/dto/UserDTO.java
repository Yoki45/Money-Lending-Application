package com.lms.system.customer.user.dto;

import com.lms.system.customer.account.dto.AccountsDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(name = "User", description = "Holds user details")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @Schema(description = "Email of the customer", example = "hjfh@gmail.com")
    private String username;

    @Schema(description = "Password of the customer", example = "1221kldfklig")
    private String password;

    @Schema(description = "Role of the customer", example = "USER")
    private String role;

    @Schema(description = "Phone of the customer", example = "0708692229")
    private String phoneNumber;

    @Schema(description = "Full name of the customer", example = "John Doe")
    private String name;

    @Schema(description = "Account details of the customer")
    private AccountsDTO accountDetails;

}
