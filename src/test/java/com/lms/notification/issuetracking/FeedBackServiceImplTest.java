package com.lms.notification.issuetracking;

import com.lms.generic.exception.BadRequestException;
import com.lms.generic.exception.NotFoundException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.system.customer.user.model.User;
import com.lms.system.notification.issuetracker.dto.FeedBackRequestDTO;
import com.lms.system.notification.issuetracker.dto.FeedBackResponseDTO;
import com.lms.system.notification.issuetracker.enums.FeedBackStatus;
import com.lms.system.notification.issuetracker.enums.FeedbackType;
import com.lms.system.notification.issuetracker.model.Feedback;
import com.lms.system.notification.issuetracker.repository.FeedBackRepository;
import com.lms.system.notification.issuetracker.service.impl.FeedBackServiceImpl;
import com.lms.system.notification.messaging.email.service.IEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FeedBackServiceImplTest {

    @Mock
    private FeedBackRepository  feedbackRepository;

    @Mock
    private ILocalizationService localizationService;

    @InjectMocks
    private FeedBackServiceImpl feedBackService;

    @Mock
    private IEmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void submitFeedback_shouldSaveFeedbackAndReturnSuccessMessage() {
        FeedBackRequestDTO requestDTO = new FeedBackRequestDTO("App not loading", "Login Issue", FeedbackType.SUPPORT);

        when(localizationService.getMessage("message.feedback.received", null)).thenReturn("Thank you for your feedback.");
        when(feedbackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        String response = feedBackService.submitFeedback(requestDTO);

        assertEquals("Thank you for your feedback.", response);
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void submitFeedback_shouldThrowBadRequestException_whenRequestIsNull() {
        when(localizationService.getMessage("message.missing.validDetails", null)).thenReturn("Missing required details");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> feedBackService.submitFeedback(null));

        assertEquals("Missing required details", exception.getMessage());
        verify(feedbackRepository, never()).save(any());
    }

    @Test
    void resolveFeedback_shouldUpdateStatusAndReturnSuccessMessage() {
        // Arrange
        User user = new User();
        user.setUsername("user@example.com");

        Feedback feedback = Feedback.builder()
                .id(1L)
                .title("Issue")
                .description("Desc")
                .type(FeedbackType.SUPPORT)
                .status(FeedBackStatus.OPEN)
                .createdBy(user)
                .build();

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(localizationService.getMessage("message.feedback.resolved", null)).thenReturn("Issue resolved");

        // Act
        String response = feedBackService.resolveFeedback(1L);

        // Assert
        assertEquals("Issue resolved", response);
        assertEquals(FeedBackStatus.CLOSED, feedback.getStatus());

        verify(feedbackRepository).save(feedback);
        verify(emailService).sendEmailIssueResolved(user, "Issue");
    }


    @Test
    void resolveFeedback_shouldThrowNotFoundException_whenFeedbackNotFound() {
        when(feedbackRepository.findById(1L)).thenReturn(Optional.empty());
        when(localizationService.getMessage("message.feedback.notFound", null)).thenReturn("Feedback not found");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> feedBackService.resolveFeedback(1L));

        assertEquals("Feedback not found", exception.getMessage());
    }

    @Test
    void getAllFeedback_shouldReturnListOfDTOs() {
        Feedback feedback1 = Feedback.builder().id(1L).title("Title1").description("Message1").type(FeedbackType.FEEDBACK).status(FeedBackStatus.OPEN).build();
        Feedback feedback2 = Feedback.builder().id(2L).title("Title2").description("Message2").type(FeedbackType.SUPPORT).status(FeedBackStatus.CLOSED).build();

        when(feedbackRepository.findAll()).thenReturn(List.of(feedback1, feedback2));

        List<FeedBackResponseDTO> responseList = feedBackService.getAllFeedback();

        assertEquals(2, responseList.size());
        assertEquals("Title1", responseList.get(0).getTitle());
        assertEquals("Message2", responseList.get(1).getMessage());
    }




}
