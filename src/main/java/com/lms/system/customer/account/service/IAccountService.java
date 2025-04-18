package com.lms.system.customer.account.service;

import com.lms.system.customer.account.dto.*;
import com.lms.system.customer.account.enums.AccountType;
import com.lms.system.customer.account.enums.TransactionType;
import com.lms.system.customer.user.model.User;

public interface IAccountService {

    void createNewAccount(User user, AccountsDTO accountsDTO);

    String depositToAccount(AccountRequestDTO accountsDTO);

    String withdrawFromAccount(AccountRequestDTO accountsDTO);

    TransactionReportDTO getTransactionReport(Long accountNumber, TransactionType transactionType, String range, Integer page);

    AccountsReportDTO getAccountDetails(Long customerId, AccountType type,Integer page);



}
