package com.lms.system.loans.service;

import com.lms.system.loans.dto.CreditScoreDTO;

import java.util.List;

public interface ICreditScoreService {

    void calculateCreditScore();

    CreditScoreDTO getUserCreditScore();


    List<CreditScoreDTO> getCreditScoreHistory(Long id);
}
