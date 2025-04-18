package com.lms.system.notifications.messaging.africastalking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SMSMessageData {
	@JsonProperty("Message")
	private String message;
	@JsonProperty("Recipients")
	private List<RecipientsDto> recipients;

}
