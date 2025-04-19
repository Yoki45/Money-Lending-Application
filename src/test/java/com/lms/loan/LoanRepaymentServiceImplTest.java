package com.lms.loan;

import com.lms.generic.exception.BadRequestException;
import com.lms.generic.exception.NotFoundException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.loan.dto.LoanRepaymentDTO;
import com.lms.system.loan.enums.LoanStatus;
import com.lms.system.loan.enums.PaymentStatus;
import com.lms.system.loan.model.Loan;
import com.lms.system.loan.model.LoanInstallment;
import com.lms.system.loan.model.LoanRepaymentHistory;
import com.lms.system.loan.repository.LoanInstallmentRepository;
import com.lms.system.loan.repository.LoanRepaymentRepository;
import com.lms.system.loan.repository.LoanRepository;

import com.lms.system.loan.service.impl.LoanRepaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanRepaymentServiceImplTest {

    @Mock
    private LoanRepaymentRepository loanRepaymentRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @Mock
    private ILocalizationService localizationService;

    @InjectMocks
    private LoanRepaymentServiceImpl loanRepaymentService;

    private Loan loan;
    private LoanInstallment installment;

    @BeforeEach
    void setUp() {
        loan = Loan.builder()
                .id(1L)
                .balance(1000.0)
                .status(LoanStatus.OPEN)
                .build();

        installment = LoanInstallment.builder()
                .id(1L)
                .loan(loan)
                .balance(500.0)
                .paymentStatus(PaymentStatus.NOT_PAID)
                .build();
    }

    @Test
    void repayLoan_shouldRepayInstallmentAndLoanBalance() {
        LoanRepaymentDTO dto = new LoanRepaymentDTO();
        dto.setLoanId(1L);
        dto.setAmount(1000.0);

        when(loanRepository.findLoanByLoanId(1L)).thenReturn(loan);
        when(loanInstallmentRepository.findLoanInstallmentByLoanAndStatus(eq(loan), eq(PaymentStatus.NOT_PAID)))
                .thenReturn(List.of(installment));
        when(loanInstallmentRepository.countUnpaidInstallmentsByLoanId(loan.getId(), PaymentStatus.NOT_PAID)).thenReturn(0L);
        when(localizationService.getMessage(eq("message.200.ok"), any())).thenReturn("Success");

        String response = loanRepaymentService.repayLoan(dto);

        assertEquals("Success", response);
        assertEquals(LoanStatus.CLOSED, loan.getStatus());
        assertEquals(0.0, loan.getBalance());
        verify(loanRepository, times(2)).save(loan);
    }


    @Test
    void repayLoan_shouldThrowBadRequest_whenDTOIsInvalid() {
        LoanRepaymentDTO dto = new LoanRepaymentDTO();
        dto.setAmount(-100.0);

        when(localizationService.getMessage(eq("message.missing.validDetails"), any())).thenReturn("Invalid");

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            loanRepaymentService.repayLoan(dto);
        });

        assertEquals("Invalid", ex.getMessage());
    }

    @Test
    void getLoanRepayments_shouldReturnRepaymentHistory() {
        Loan loan = Loan.builder().id(1L).build();
        LoanRepaymentHistory history = LoanRepaymentHistory.builder()
                .loan(loan)
                .amount(200.0)
                .build();

        when(loanRepository.findLoanByLoanId(1L)).thenReturn(loan);
        when(loanRepaymentRepository.findRepaymentHistoryByLoans(any()))
                .thenReturn(List.of(history));

        List<LoanRepaymentDTO> results = loanRepaymentService.getLoanRepayments(1L);

        assertEquals(1, results.size());
        assertEquals(200.0, results.get(0).getAmount());
    }

    @Test
    void getLoanRepayments_shouldThrowException_whenLoanNotFound() {
        when(loanRepository.findLoanByLoanId(anyLong())).thenReturn(null);
        when(localizationService.getMessage(eq("message.loan.notFound"), any())).thenReturn("Not Found");

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            loanRepaymentService.getLoanRepayments(1L);
        });

        assertEquals("Not Found", ex.getMessage());
    }


}
