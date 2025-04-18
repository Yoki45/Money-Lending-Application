package com.lms.system.customer.account.service.impl;

import com.lms.generic.dateRange.model.DateRangeFilter;
import com.lms.generic.dateRange.service.DateFilterRangeService;
import com.lms.generic.exception.BadRequestException;
import com.lms.generic.exception.NotFoundException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.customer.account.dto.*;
import com.lms.system.customer.account.enums.AccountType;
import com.lms.system.customer.account.enums.ActiveStatus;
import com.lms.system.customer.account.enums.TransactionType;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.model.Transaction;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.account.repository.TransactionRepository;
import com.lms.system.customer.account.service.IAccountService;
import com.lms.system.customer.user.model.User;
import com.lms.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements IAccountService {

    private final AccountRepository accountRepository;

    private final ILocalizationService localizationService;

    private final TransactionRepository transactionRepository;

    private final DateFilterRangeService dateFilterRangeService;


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

    @Override
    public String depositToAccount(AccountRequestDTO accountsDTO) {

        validateAccountsDepositRequest(accountsDTO);

        Account account = accountRepository.findAccountByAccountNumber(accountsDTO.getAccountNumber());

        if(account == null){
            throw new NotFoundException(localizationService.getMessage("message.account.NotFound", null));
        }

        Double depositAmount = accountsDTO.getAmount();

        account.setBalance(account.getBalance() + depositAmount);
        account.setDeposits(account.getDeposits() + depositAmount);

        if (account.getActiveStatus().equals(ActiveStatus.INACTIVE)) {
            account.setActiveStatus(ActiveStatus.ACTIVE);
        }

        account = accountRepository.save(account);

        createAndSaveTransaction(account, depositAmount, TransactionType.DEPOSIT);

        return localizationService.getMessage("message.account.depositSuccess",
                new Object[]{depositAmount, account.getAccountNumber()});


    }

    @Override
    public String withdrawFromAccount(AccountRequestDTO accountsDTO) {

        if (accountsDTO == null) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails", null));
        }

        Account account = accountRepository.findAccountByAccountNumber(accountsDTO.getAccountNumber());

        if(account == null){
            throw new NotFoundException(localizationService.getMessage("message.account.NotFound", null));
        }

        Double depositAmount = accountsDTO.getAmount();

        if (account.getBalance() < depositAmount) {
            throw new BadRequestException(localizationService.getMessage("message.insufficientBalance",
                    new Object[]{account.getAccountNumber(), depositAmount}));
        }

        account.setBalance(account.getBalance() - depositAmount);
        account.setDeposits(account.getWithdrawals() + depositAmount);
        accountRepository.save(account);

        createAndSaveTransaction(account, depositAmount, TransactionType.WITHDRAWAL);

        return localizationService.getMessage("message.account.withdrawalSuccess",
                new Object[]{depositAmount, account.getAccountNumber()});


    }

    @Override
    public TransactionReportDTO getTransactionReport(Long accountNumber, TransactionType transactionType, String range, Integer page) {

        int currentPage = Optional.ofNullable(page).orElse(1);

        range = Optional.ofNullable(range).orElse("this_week");

        DateRangeFilter rangeFilter = dateFilterRangeService.getFilterDateRange(range);

        List<Transaction> transactions = transactionRepository.findTransactions(accountNumber, transactionType,
                rangeFilter.getBeginCalendar().getTime(), rangeFilter.getEndCalendar().getTime());

        List<List<Transaction>> totalPages = Utils.createSubList(transactions, Utils.MAX_PAGE_SIZE);

        if (totalPages.isEmpty()) {
            return new TransactionReportDTO(currentPage, 0, Collections.emptyList(),0d,0d);
        }

        List<TransactionsResponseDTO> content = totalPages.get(currentPage - 1)
                .stream()
                .map(this::mapToTransactionResponseDTO)
                .collect(Collectors.toList());

        Double totalDeposits = content.stream()
                .filter(transactionsResponseDTO -> TransactionType.DEPOSIT.equals(transactionsResponseDTO.getTransactionType()))
                .mapToDouble(TransactionsResponseDTO::getAmount)
                .sum();

        Double totalWithDrawals = content.stream()
                .filter(transactionsResponseDTO -> TransactionType.WITHDRAWAL.equals(transactionsResponseDTO.getTransactionType()))
                .mapToDouble(TransactionsResponseDTO::getAmount)
                .sum();


        return new TransactionReportDTO(currentPage, totalPages.size(), content,totalDeposits,totalWithDrawals);
    }

    private TransactionsResponseDTO mapToTransactionResponseDTO(Transaction transaction) {
        TransactionsResponseDTO transactionsResponseDTO = new TransactionsResponseDTO();
        transactionsResponseDTO.setTransactionId(transaction.getId());
        transactionsResponseDTO.setAmount(transaction.getAmount());
        transactionsResponseDTO.setTransactionType(transaction.getTransactionType());
        return transactionsResponseDTO;

    }


    @Override
    public AccountsReportDTO getAccountDetails(Long customerId, AccountType type, Integer page) {
        validateRequest(customerId, type);

        int currentPage = Optional.ofNullable(page).orElse(1);

        List<Account> accounts = accountRepository.findAccounts(customerId, type);
        List<List<Account>> totalPages = Utils.createSubList(accounts, Utils.MAX_PAGE_SIZE);

        if (totalPages.isEmpty()) {
            return new AccountsReportDTO(currentPage, 0, Collections.emptyList());
        }

        List<AccountResponseDTO> content = totalPages.get(currentPage - 1)
                .stream()
                .map(this::mapToAccountResponseDTO)
                .collect(Collectors.toList());

        return new AccountsReportDTO(currentPage, totalPages.size(), content);
    }

    private void validateRequest(Long customerId, AccountType type) {
        if (customerId == null && type == null) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails", null));
        }
    }

    private AccountResponseDTO mapToAccountResponseDTO(Account account) {
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
        accountResponseDTO.setAccountNumber(account.getAccountNumber());
        accountResponseDTO.setAccountType(account.getAccountType());
        accountResponseDTO.setBranchAddress(account.getBranchAddress());
        accountResponseDTO.setBalance(account.getBalance());
        accountResponseDTO.setDeposit(account.getDeposits());
        accountResponseDTO.setWithdrawal(account.getWithdrawals());
        return accountResponseDTO;
    }


    private void createAndSaveTransaction(Account account, Double depositAmount, TransactionType transactionType) {
        transactionRepository.save(Transaction.builder()
                .account(account)
                .amount(depositAmount)
                .transactionType(transactionType)
                .build());
    }

    private void validateAccountsDepositRequest(AccountRequestDTO accountsDTO) {
        if (accountsDTO == null) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails", null));
        }

        if (accountsDTO.getAmount() == null || accountsDTO.getAmount() <= 0) {
            throw new BadRequestException(localizationService.getMessage("message.invalid.depositAmount",
                    new Object[]{accountsDTO.getAmount()}));
        }
    }

}
