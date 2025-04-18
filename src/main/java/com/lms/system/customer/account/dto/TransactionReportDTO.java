package com.lms.system.customer.account.dto;

import com.lms.generic.dto.PageInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(name = "Transaction Report",description = "Holds transaction information of an account")
public class TransactionReportDTO extends PageInfoDTO {

    private Double totalDeposits;

    private Double totalWithdrawals;

    private  Double totalAmount;

    private List<TransactionsResponseDTO> transactions;

    public TransactionReportDTO(int currentPage, int totalPages, List<TransactionsResponseDTO> transactions, Double totalDeposits, Double totalWithdrawals) {
        super(currentPage, totalPages);
        this.transactions = transactions;
        this.totalDeposits = totalDeposits;
        this.totalWithdrawals = totalWithdrawals;
        this.totalAmount = totalDeposits + totalWithdrawals;
    }
}
