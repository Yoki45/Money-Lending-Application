package com.lms.system.notifications.kafka.consumer.loan;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.annotation.KafkaListener;

public interface ILoanConsumerService {


    @KafkaListener(topics = {"loan_creation"}, containerFactory = "loanCreationKafkaListenerContainerFactory")
    void processLoanRequest(String message) throws JsonProcessingException;


}
