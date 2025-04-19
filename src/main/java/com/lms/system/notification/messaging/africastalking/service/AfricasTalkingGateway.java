package com.lms.system.notification.messaging.africastalking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.system.customer.user.model.User;
import com.lms.system.notification.messaging.africastalking.dto.MessageDto;
import com.lms.system.notification.messaging.africastalking.dto.SmsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AfricasTalkingGateway {

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper;

    @Value("${africas.username}")
    private String username;
    @Value("${africas.apikey}")
    private String apiKey;
    @Value("${africas.base.url}")
    private String baseUrl;

    public String formatAfricasPhoneNumbers(String number) {
        number = number.replaceAll("\\s+", "");
        if (number.contains("\\+") && number.length() == 13) {
            return number;
        } else if (number.length() == 12) {
            return "+" + number;
        } else if (number.length() == 10) {
            return "+254" + number.substring(1);
        } else {
            return number;
        }
    }

    public SmsResponseDto sendSMS(MessageDto messageDto, User toUser) {
        try {
            messageDto.setUsername(username);
            messageDto.setTo(formatAfricasPhoneNumbers(messageDto.getTo()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("apiKey", apiKey);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("username", messageDto.getUsername());
            body.add("to", messageDto.getTo());
            body.add("message", messageDto.getMessage());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                return objectMapper.readValue(response.getBody(), SmsResponseDto.class);
            } else {
                log.warn("Failed to send SMS to {}. Status: {}, Body: {}",
                        messageDto.getTo(), response.getStatusCode(), response.getBody());
            }

        } catch (Exception e) {
            log.error(" Error while sending SMS to {}: {}", messageDto.getTo(), e.getMessage(), e);
        }

        return new SmsResponseDto();
    }

    @Async
    public void sendMultiple(List<MessageDto> messageDtos, User toUser) {
        if (messageDtos == null || messageDtos.isEmpty()) {
            return;
        }

        messageDtos.forEach(message -> {
            sendSMS(message, toUser);
        });
    }


    public void sentReminderMessages(List<User> customers) {
        List<MessageDto> reminderMessages = new ArrayList<>();
        customers = customers.stream().distinct().collect(Collectors.toList());
        for (User user : customers) {
            MessageDto message = new MessageDto();
            message.setTo(user.getPhone());
            message.setMessage("Friendly Reminder: Your loan repayment is one week due . Please ensure timely payment to avoid any delays or penalties. Thank you!");
            reminderMessages.add(message);
        }

        this.sendMultiple(reminderMessages, null);
        log.info("Sent reminder messages: {}", reminderMessages.size());

    }


}


