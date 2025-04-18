package com.lms.customer.account;

import com.lms.generic.exception.BadRequestException;
import com.lms.generic.exception.NotFoundException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.customer.account.dto.AccountRequestDTO;
import com.lms.system.customer.account.dto.AccountsDTO;
import com.lms.system.customer.account.enums.AccountType;
import com.lms.system.customer.account.enums.ActiveStatus;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.model.Transaction;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.account.repository.TransactionRepository;
import com.lms.system.customer.account.service.impl.AccountServiceImpl;
import com.lms.system.customer.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {


    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ILocalizationService localizationService;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void createNewAccount_ShouldSaveNewAccount_WhenValidInput() {
        User user = new User();
        AccountsDTO accountsDTO = new AccountsDTO();
        accountsDTO.setAccountType(AccountType.SAVINGS);
        accountsDTO.setBranchAddress("Main Branch");

        accountService.createNewAccount(user, accountsDTO);

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void depositToAccount_ShouldThrowBadRequest_WhenRequestIsNull() {
        assertThrows(BadRequestException.class, () -> accountService.depositToAccount(null));
    }

    @Test
    void depositToAccount_ShouldThrowNotFound_WhenAccountDoesNotExist() {
        AccountRequestDTO dto = new AccountRequestDTO();
        dto.setAccountNumber(12345L);
        dto.setAmount(100.0);

        when(accountRepository.findAccountByAccountNumber(dto.getAccountNumber())).thenReturn(null);
        when(localizationService.getMessage("message.account.NotFound", null))
                .thenReturn("Account not found");

        assertThrows(NotFoundException.class, () -> accountService.depositToAccount(dto));
    }

    @Test
    void withdrawFromAccount_ShouldThrowBadRequest_WhenRequestIsNull() {
        when(localizationService.getMessage("message.missing.validDetails", null))
                .thenReturn("Missing valid details");

        assertThrows(BadRequestException.class, () -> accountService.withdrawFromAccount(null));
    }

    @Test
    void withdrawFromAccount_ShouldThrowNotFound_WhenAccountDoesNotExist() {
        AccountRequestDTO dto = new AccountRequestDTO();
        dto.setAccountNumber(1L);
        dto.setAmount(500.0);

        when(accountRepository.findAccountByAccountNumber(dto.getAccountNumber())).thenReturn(null);
        when(localizationService.getMessage("message.account.NotFound", null))
                .thenReturn("Account not found");

        assertThrows(NotFoundException.class, () -> accountService.withdrawFromAccount(dto));
    }

    @Test
    void withdrawFromAccount_ShouldThrowBadRequest_WhenInsufficientBalance() {
        AccountRequestDTO dto = new AccountRequestDTO();
        dto.setAccountNumber(1L);
        dto.setAmount(500.0);

        Account account = new Account();
        dto.setAccountNumber(1L);
        account.setBalance(100.0);

        when(accountRepository.findAccountByAccountNumber(dto.getAccountNumber())).thenReturn(account);
        when(localizationService.getMessage(eq("message.insufficientBalance"), any()))
                .thenReturn("Insufficient balance");

        assertThrows(BadRequestException.class, () -> accountService.withdrawFromAccount(dto));
    }


    @Test
    void depositToAccount_ShouldDepositAndActivateAccount_WhenValidRequest() {
        AccountRequestDTO dto = new AccountRequestDTO();
        dto.setAccountNumber(1L);
        dto.setAmount(500.0);

        Account account = new Account();
        account.setAccountNumber(1L);
        account.setBalance(1000.0);
        account.setDeposits(2000.0);
        account.setActiveStatus(ActiveStatus.INACTIVE);

        when(accountRepository.findAccountByAccountNumber(dto.getAccountNumber()))
                .thenReturn(account);

        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);

        when(localizationService.getMessage(eq("message.account.depositSuccess"), any()))
                .thenReturn("Deposit successful");

        String message = accountService.depositToAccount(dto);

        assertEquals("Deposit successful", message);
        assertEquals(1500.0, account.getBalance());
        assertEquals(2500.0, account.getDeposits());
        assertEquals(ActiveStatus.ACTIVE, account.getActiveStatus());

        verify(transactionRepository).save(any(Transaction.class));
    }


    @Test
    void withdrawFromAccount_ShouldWithdrawAndReturnSuccessMessage_WhenSufficientBalance() {
        AccountRequestDTO dto = new AccountRequestDTO();
        dto.setAccountNumber(1L);
        dto.setAmount(50.0);

        Account account = new Account();
        account.setAccountNumber(1L);
        account.setBalance(100.0);
        account.setDeposits(200.0);

        when(accountRepository.findAccountByAccountNumber(dto.getAccountNumber()))
                .thenReturn(account);

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        when(localizationService.getMessage(eq("message.account.withdrawalSuccess"), any()))
                .thenReturn("Withdrawal successful");

        String result = accountService.withdrawFromAccount(dto);

        assertEquals("Withdrawal successful", result);
        assertEquals(50.0, account.getBalance());

        verify(transactionRepository).save(any(Transaction.class));
    }





}
