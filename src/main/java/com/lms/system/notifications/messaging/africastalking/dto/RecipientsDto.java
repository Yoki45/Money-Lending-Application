package com.lms.system.notifications.messaging.africastalking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipientsDto {

	private String number;
	private String status;
	private int statusCode;
	private String cost;
	private String messageId;

}
