package com.lms.system.notification.messaging.email.service;

import com.lms.system.customer.user.model.User;
import com.lms.system.loan.dto.LoanRequestDTO;
import com.lms.system.loan.model.CreditScore;

import java.time.LocalDate;

public interface IEmailService {

    void sendEmail(String emailAddress, String subject,String message);

    void sendLoanApprovalEmail(LoanRequestDTO request, User user, LocalDate firstDueDate);

    void sendEmailRejectionDueToLowCreditScore(User user, CreditScore creditScore);

}





