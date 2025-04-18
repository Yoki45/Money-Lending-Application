package com.lms.system.loans.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.generic.exception.BadRequestException;
import com.lms.generic.exception.NotFoundException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.user.model.User;
import com.lms.system.loans.dto.LoanRequestDTO;
import com.lms.system.loans.enums.LoanRiskCategory;
import com.lms.system.loans.model.Loan;
import com.lms.system.loans.model.LoanLimit;
import com.lms.system.loans.repository.LoanLimitRepository;
import com.lms.system.loans.repository.LoanRepository;
import com.lms.system.loans.service.ILoanService;
import com.lms.system.product.model.Product;
import com.lms.system.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements ILoanService {

    private final LoanRepository loanRepository;

    private final ILocalizationService localizationService;

    private final AccountRepository accountRepository;

    private final LoanLimitRepository loanLimitRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    private final ProductRepository productRepository;


    @Override
    public String requestForLoan(LoanRequestDTO loanRequestDTO) throws JsonProcessingException {

        if (loanRequestDTO == null) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails", null));
        }

        Product product = productRepository.findById(Long.valueOf(loanRequestDTO.getProductId()))
                .orElseThrow(() ->
                        new NotFoundException(localizationService.getMessage("message.product.NotFound", null)));


        List<Loan> overdueLoans = loanRepository.
                findLoanByAccountNumberAndProduct(loanRequestDTO.getAccountNumber(), loanRequestDTO.getProductId(), new ArrayList<>());

        if (overdueLoans.size() > 0) {
            throw new BadRequestException(localizationService.getMessage("message.loans.pendingOverdue", null));
        }

        User user = accountRepository.findCustomerByAccountNumber(loanRequestDTO.getAccountNumber());

        LoanLimit loanLimit = loanLimitRepository.findLoanLimitByUser(user);

        Double existingLoans = loanRepository.totalLoansByAccountNumber(loanRequestDTO.getAccountNumber(), new ArrayList<>());

        if (loanLimit == null || loanLimit.getCategory() == LoanRiskCategory.INELIGIBLE) {
            throw new BadRequestException(localizationService.getMessage("message.loan.enligible", null));
        }

        if (loanLimit.getLimit() <= (existingLoans + loanRequestDTO.getAmount())) {
            throw new BadRequestException(localizationService.getMessage("message.loans.exceededLoanLimit", null));
        }


        String message = mapper.writeValueAsString(loanRequestDTO);
        kafkaTemplate.send("loan_creation", message);
        log.info("Loan application published for account {}: {}", loanRequestDTO.getAccountNumber(), message);

        return localizationService.getMessage("message.loans.created", null);
    }
}
