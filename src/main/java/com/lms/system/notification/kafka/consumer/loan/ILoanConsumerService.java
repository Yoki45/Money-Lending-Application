package com.lms.system.notification.kafka.consumer.loan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lms.system.loan.dto.LoanRequestDTO;
import org.springframework.kafka.annotation.KafkaListener;

public interface ILoanConsumerService {


    @KafkaListener(topics = {"loan_creation"}, containerFactory = "loanCreationKafkaListenerContainerFactory")
    void processLoanRequest(LoanRequestDTO loanRequestDTO) throws JsonProcessingException;


}
