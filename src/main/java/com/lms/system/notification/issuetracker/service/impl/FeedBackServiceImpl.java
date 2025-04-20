package com.lms.system.notification.issuetracker.service.impl;

import com.lms.generic.audit.AuditAwareImpl;
import com.lms.generic.exception.BadRequestException;
import com.lms.generic.exception.NotFoundException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.notification.issuetracker.dto.FeedBackRequestDTO;
import com.lms.system.notification.issuetracker.dto.FeedBackResponseDTO;
import com.lms.system.notification.issuetracker.enums.FeedBackStatus;
import com.lms.system.notification.issuetracker.model.Feedback;
import com.lms.system.notification.issuetracker.repository.FeedBackRepository;
import com.lms.system.notification.issuetracker.service.IFeedBackService;
import com.lms.system.notification.messaging.email.service.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedBackServiceImpl implements IFeedBackService {


    private final FeedBackRepository feedbackRepository;

    private final ILocalizationService localizationService;

    private final IEmailService emailService;

    private final AuditAwareImpl auditAware;


    @Override
    public String submitFeedback(FeedBackRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails", null));
        }

        Feedback feedback = Feedback.builder()
                .description(requestDTO.getMessage())
                .title(requestDTO.getTitle())
                .type(requestDTO.getType())
                .status(FeedBackStatus.OPEN)
                .createdBy(auditAware.getCurrentLoggedInUser())
                .createdOn(new Date())
                .build();

         feedback = feedbackRepository.save(feedback);


        return localizationService.getMessage("message.feedback.received", null);
    }

    @Override
    public String resolveFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id).orElseThrow(() ->
                new NotFoundException(localizationService.getMessage("message.feedback.notFound", null)));

        feedback.setStatus(FeedBackStatus.CLOSED);
        feedbackRepository.save(feedback);

        emailService.sendEmailIssueResolved(feedback.getCreatedBy(),feedback.getTitle());

        return localizationService.getMessage("message.feedback.resolved", null);
    }

    @Override
    public List<FeedBackResponseDTO> getAllFeedback() {
        return feedbackRepository.findAll().stream().map(feedback ->
                FeedBackResponseDTO.builder()
                        .id(feedback.getId())
                        .type(feedback.getType())
                        .message(feedback.getDescription())
                        .title(feedback.getTitle())
                        .resolved(feedback.getStatus())
                        .build()
        ).toList();
    }


}
