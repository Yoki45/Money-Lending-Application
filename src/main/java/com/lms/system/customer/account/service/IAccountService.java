package com.lms.system.customer.account.service;

import com.lms.system.customer.account.dto.AccountsDTO;
import com.lms.system.customer.user.model.User;

public interface IAccountService {

    void createNewAccount(User user, AccountsDTO accountsDTO);
}
