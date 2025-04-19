package com.lms.system.loan.service;

import com.lms.system.loan.dto.CreditScoreDTO;

import java.util.List;

public interface ICreditScoreService {

    void calculateCreditScore();

    CreditScoreDTO getUserCreditScore();


    List<CreditScoreDTO> getCreditScoreHistory(Long id);
}
