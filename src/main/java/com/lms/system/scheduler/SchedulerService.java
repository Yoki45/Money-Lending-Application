package com.lms.system.scheduler;

import com.lms.system.loan.service.ICreditScoreService;
import com.lms.system.loan.service.ILoanLimitService;
import com.lms.system.loan.service.ILoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final ICreditScoreService creditScoreService;

    private final ILoanLimitService loanLimitService;

    private final ILoanService loanService;

    @Value("${update_credit_scores}")
    private boolean updateCreditScores;

    @Value("${update_loan_limit}")
    private boolean updateLoanLimit;

    @Value("${update_overdue_loans}")
    private boolean updateOverDueLoans;

    @Value("${update_late_fees}")
    private boolean updateLateFees;

    @Value("${sent_due_date_reminders}")
    private boolean sentReminders;

    private final AtomicBoolean busyUpdateCreditScore = new AtomicBoolean(false);
    private final AtomicBoolean busyUpdateLoanLimit = new AtomicBoolean(false);
    private final AtomicBoolean busyUpdateOverdueLoans = new AtomicBoolean(false);
    private final AtomicBoolean busyUpdateLateFees = new AtomicBoolean(false);
    private final AtomicBoolean busySentReminders = new AtomicBoolean(false);


    @Scheduled(cron = "0 0 23 30 * ?") // At 23:00 on the 30th day of every month
    public void updateCreditScoresMonthly() {
        if (!updateCreditScores || !busyUpdateCreditScore.compareAndSet(false, true)) {
            System.out.println("Spring Scheduler: Skipping Credit Score Update - " + new Date() + " :canceled_busy:");
            return;
        }

        try {
            creditScoreService.calculateCreditScore();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            busyUpdateCreditScore.set(false);
        }
    }


    @Scheduled(cron = "0 0 23 3 * ?") // At 23:00 on the 3rd day of every month
    public void updateLoanLimitMonthly() {
        if (!updateLoanLimit || !busyUpdateLoanLimit.compareAndSet(false, true)) {
            System.out.println("Spring Scheduler: Skipping Loan limit Update - " + new Date() + " :canceled_busy:");
            return;
        }

        try {
            loanLimitService.calculateLoanLimit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            busyUpdateLoanLimit.set(false);
        }
    }

    @Scheduled(cron = "0 0 12 * * ?") // Runs daily at 12 PM
    public void updateOverDueLoans() {
        if (!updateOverDueLoans || !busyUpdateOverdueLoans.compareAndSet(false, true)) {
            System.out.println("Spring Scheduler: over due loans Update - " + new Date() + " :canceled_busy:");
            return;
        }

        try {
            loanService.sweepOverdueLoans();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            busyUpdateOverdueLoans.set(false);
        }
    }


    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void applyLateFeesToOverdueLoans() {
        if (!updateLateFees || !busyUpdateLateFees.compareAndSet(false, true)) {
            System.out.println("Spring Scheduler: apply late fees - " + new Date() + " :canceled_busy:");
            return;
        }

        try {
            loanService.applyLateFeesToOvedueLoans();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            busyUpdateLateFees.set(false);
        }
    }

    @Scheduled(cron = "0 30 8 * * ?") // Every day at 8:30 AM
    public void sendDueDateReminders() {
        if (!sentReminders || !busySentReminders.compareAndSet(false, true)) {
            System.out.println("Spring Scheduler: sending of Reminders - " + new Date() + " :canceled_busy:");
            return;
        }

        try {
            loanService.sendDueDateReminders();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            busySentReminders.set(false);
        }
    }


}
