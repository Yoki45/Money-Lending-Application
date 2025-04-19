package com.lms.system.notification.messaging.africastalking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsResponseDto {
	@JsonProperty("SMSMessageData")
	private SMSMessageData smsmessageData;

}
