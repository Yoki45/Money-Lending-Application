package com.lms.system.notification.messaging.africastalking.controller;

import com.lms.generic.audit.AuditAwareImpl;
import com.lms.generic.dto.ErrorResponseDTO;

import com.lms.system.customer.user.model.User;
import com.lms.system.notification.messaging.africastalking.dto.MessageDto;
import com.lms.system.notification.messaging.africastalking.dto.SmsResponseDto;
import com.lms.system.notification.messaging.africastalking.service.AfricasTalkingGateway;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/africas-talking", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "Testing africas talking sms")
@RequiredArgsConstructor
public class AfricasStalkingController {

     private final AuditAwareImpl auditorAware;

     private final AfricasTalkingGateway africasTalkingGateway;


    @Operation(summary = "update credit score manually")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @PutMapping()
    public ResponseEntity<SmsResponseDto> testAfricasStalking() {
        User user = auditorAware.getCurrentLoggedInUser();
        MessageDto message = new MessageDto();
        message.setTo(user.getPhone());
        message.setMessage("This is a test message");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(africasTalkingGateway.sendSMS(message, user));

    }




}
