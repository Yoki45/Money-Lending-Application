package com.lms.system.customer.account.dto;

import com.lms.generic.dto.PageInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Schema(name = "Accounts Report",description = "Holds accounts information of a customer")
public class AccountsReportDTO extends PageInfoDTO {

    List<AccountResponseDTO> accounts;

    public AccountsReportDTO(int currentPage, int totalPages, List<AccountResponseDTO> accounts) {
        super(currentPage, totalPages);
        this.accounts = accounts;
    }
}
