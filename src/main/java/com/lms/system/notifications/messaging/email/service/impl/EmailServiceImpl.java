package com.lms.system.notifications.messaging.email.service.impl;

import com.lms.system.customer.user.model.User;
import com.lms.system.loans.dto.LoanRequestDTO;
import com.lms.system.loans.model.CreditScore;
import com.lms.system.notifications.messaging.email.service.IEmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;

import static com.lms.utils.Utils.MINIMUM_CREDIT_SCORE;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String emailAddress, String subject, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(emailAddress);
            helper.setSubject(subject);
            helper.setText(message, true);

            mailSender.send(mimeMessage);

            log.info("Email successfully sent to {}", emailAddress);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", emailAddress, e.getMessage(), e);
        }
    }

    public void sendEmailRejectionDueToLowCreditScore(User user, CreditScore creditScore) {

        String subject = " Loan Application Status: Rejected";

        String htmlBody = """
                <html>
                  <body style="font-family: Arial, sans-serif; color: #333;">
                    <p>Dear %s,</p>

                    <p>We hope this message finds you well.</p>

                    <p>
                      We regret to inform you that your recent loan application has been <strong>rejected</strong> due to a low credit score.
                      At this time, your score does not meet the minimum threshold required to qualify for a loan under your current profile.
                    </p>

                    <p><strong>Credit Score Details:</strong></p>
                    <ul>
                        <li><strong>Minimum Required Credit Score:</strong> %d</li>
                        <li><strong>Your Current Credit Score:</strong> %.2f</li>
                    </ul>

                    <p>
                      We encourage you to continue engaging with your account through consistent deposits,
                      timely repayments, and by minimizing overdue loans to improve your score for future applications.
                    </p>

                    <p>
                      If you have any questions or need assistance, feel free to contact our support team.
                    </p>

                    <p>Thank you for using our services.</p>

                    <p>Best regards,<br/>
                    <strong>Loan Management Team</strong><br/>
                    LMS service</p>
                  </body>
                </html>
                """.formatted(
                user.getName(),
                MINIMUM_CREDIT_SCORE,
                creditScore.getScore()
        );

        this.sendEmail(user.getUsername(), subject, htmlBody);
    }


    public void sendLoanApprovalEmail(LoanRequestDTO request, User user, LocalDate firstDueDate) {
        String subject = " Loan Application Approved";

        String htmlBody = """
                <html>
                  <body style="font-family: Arial, sans-serif; color: #333;">
                    <p>Dear %s,</p>

                    <p>Congratulations! We are pleased to inform you that your loan application has been <strong>approved</strong>.</p>

                    <p><strong>Here are the details:</strong></p>
                    <ul>
                        <li><strong>Approved Amount:</strong> KES %.2f</li>
                        <li><strong>Repayment Due Date:</strong> %s</li>
                    </ul>

                    <p>Please ensure timely repayment to maintain a healthy credit score and avoid any penalties.</p>

                    <p>You can view the full loan details by logging into your account.</p>

                    <p>Should you have any questions, feel free to reach out to our support team.</p>

                    <p>Thank you for choosing <strong>LMS service</strong>.</p>

                    <p>Warm regards,<br/>
                    <strong>Loan Management Team</strong></p>
                  </body>
                </html>
                """.formatted(
                user.getName(),
                request.getAmount(),
                firstDueDate.toString()
        );

        this.sendEmail(user.getUsername(), subject, htmlBody);
    }
}
