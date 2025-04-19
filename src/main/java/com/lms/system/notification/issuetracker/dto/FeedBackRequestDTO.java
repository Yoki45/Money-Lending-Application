package com.lms.system.notification.issuetracker.dto;

import com.lms.system.notification.issuetracker.enums.FeedbackType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedBackRequestDTO {

    @Schema(description = "Type of issue: SUPPORT, FEEDBACK, BUG, COMPLAINT", example = "SUPPORT")
    private FeedbackType type;

    @Schema(description = "User message or issue details", example = "I can't log into my account")
    private String message;

    @Schema(description = " Title of the message", example = " Account Credentials")
    private String title;

    public FeedBackRequestDTO(String message, String title, FeedbackType type) {
        this.message = message;
        this.title = title;
        this.type = type;
    }



}
