package com.lms.system.scheduler;

import com.lms.system.loan.service.ICreditScoreService;
import com.lms.system.loan.service.ILoanLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private  final ICreditScoreService creditScoreService;

    private final ILoanLimitService loanLimitService;

    @Value("${update_credit_scores}")
    private boolean updateCreditScores;

    private final AtomicBoolean busyUpdateCreditScore = new AtomicBoolean(false);


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
        if (!updateCreditScores || !busyUpdateCreditScore.compareAndSet(false, true)) {
            System.out.println("Spring Scheduler: Skipping Loan limit Update - " + new Date() + " :canceled_busy:");
            return;
        }

        try {
            loanLimitService.calculateLoanLimit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            busyUpdateCreditScore.set(false);
        }
    }


}
