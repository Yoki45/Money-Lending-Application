package com.lms.system.customer.account.service.impl;


import com.lms.system.customer.account.dto.AccountsDTO;
import com.lms.system.customer.account.enums.ActiveStatus;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.account.service.IAccountService;
import com.lms.system.customer.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService {

    private final AccountRepository accountRepository;


    @Override
    public void createNewAccount(User user, AccountsDTO accountsDTO) {

        Account newAccount = new Account();

        newAccount.setCustomer(user);
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(accountsDTO.getAccountType());
        newAccount.setBranchAddress(accountsDTO.getBranchAddress());
        newAccount.setActiveStatus(ActiveStatus.INACTIVE);

        accountRepository.save(newAccount);

    }
}
