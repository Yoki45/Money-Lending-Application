package com.lms.utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final String STATUS_201 = "201";
    public static final String STATUS_200 = "200";
    public static final int MAX_PAGE_SIZE = 50;

   public static final int DEPOSIT_THRESHOLD_1 = 1000;
   public static final int DEPOSIT_THRESHOLD_2 = 5000;
   public static final int DEPOSIT_THRESHOLD_3 = 10000;

    public  static final double DEPOSIT_BONUS_1_PERCENT = 0.02; // 2% bonus for deposits > 1000
    public  static final double DEPOSIT_BONUS_2_PERCENT = 0.05; // 5% bonus for deposits > 5000
    public  static final double DEPOSIT_BONUS_3_PERCENT = 0.10; // 10% bonus for deposits > 10000
    public  static final double ON_TIME_REPAYMENT_BONUS_PERCENT = 0.015; // 1.5% bonus per on-time repayment
    public static final double LATE_REPAYMENT_PENALTY_PERCENT = 0.025; // 2.5% penalty per late repayment
    public  static final double TRANSACTION_BONUS_1_PERCENT = 0.025; // 2.5% bonus for 10-20 transactions
    public static final double TRANSACTION_BONUS_2_PERCENT = 0.05; // 5% bonus for more than 20 transactions

    public static final double MAX_SCORE = 850.0;

    public static final double MINIMUM_CREDIT_SCORE = 600.0;

    public static final double MULTIPLIER_LOW = 0.8;
    public static final double MULTIPLIER_MEDIUM = 0.6;
    public static final double MULTIPLIER_ELEVATED = 0.4;
    public static final double MULTIPLIER_HIGH = 0.2;
    public static final double MULTIPLIER_INELIGIBLE = 0.0;


    public static <T> List<List<T>> createSubList(List<T> list, int subListSize) {
        List<List<T>> listOfSubList = new ArrayList<>();
        for (int i = 0; i < list.size(); i += subListSize) {
            if (i + subListSize <= list.size()) {
                listOfSubList.add(list.subList(i, i + subListSize));
            } else {
                listOfSubList.add(list.subList(i, list.size()));
            }
        }
        return listOfSubList;
    }

    public static boolean stringNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }




}

