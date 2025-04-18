package com.lms.system.notifications.messaging.email.service;

import com.lms.system.customer.user.model.User;
import com.lms.system.loans.dto.LoanRequestDTO;
import com.lms.system.loans.model.CreditScore;

import java.time.LocalDate;

public interface IEmailService {

    void sendEmail(String emailAddress, String subject,String message);

    void sendLoanApprovalEmail(LoanRequestDTO request, User user, LocalDate firstDueDate);

    void sendEmailRejectionDueToLowCreditScore(User user, CreditScore creditScore);

}





