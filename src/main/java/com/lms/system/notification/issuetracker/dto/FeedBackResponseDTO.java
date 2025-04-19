package com.lms.system.notification.issuetracker.dto;

import com.lms.system.notification.issuetracker.enums.FeedBackStatus;
import com.lms.system.notification.issuetracker.enums.FeedbackType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackResponseDTO {

    @Schema(description = "Feedback ID", example = "1")
    private Long id;

    @Schema(description = "Type of feedback", example = "BUG")
    private FeedbackType type;

    @Schema(description = "Message provided by the user", example = "Page crashes on submit")
    private String message;

    @Schema(description = "Whether the issue has been resolved", example = "OPEN")
    private FeedBackStatus resolved;

    @Schema(description = "User who submitted the issue", example = "johndoe")
    private String title;


}
