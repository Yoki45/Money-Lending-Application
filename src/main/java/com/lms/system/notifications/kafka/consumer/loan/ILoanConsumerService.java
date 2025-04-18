package com.lms.system.notifications.kafka.consumer.loan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lms.system.loans.dto.LoanRequestDTO;
import org.springframework.kafka.annotation.KafkaListener;

public interface ILoanConsumerService {


    @KafkaListener(topics = {"loan_creation"}, containerFactory = "loanCreationKafkaListenerContainerFactory")
    void processLoanRequest(LoanRequestDTO loanRequestDTO) throws JsonProcessingException;


}
