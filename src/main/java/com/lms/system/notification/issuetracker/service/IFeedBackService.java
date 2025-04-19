package com.lms.system.notification.issuetracker.service;

import com.lms.system.notification.issuetracker.dto.FeedBackRequestDTO;
import com.lms.system.notification.issuetracker.dto.FeedBackResponseDTO;

import java.util.List;

public interface IFeedBackService {

    String submitFeedback(FeedBackRequestDTO requestDTO);

    String resolveFeedback(Long id);

    List<FeedBackResponseDTO> getAllFeedback();

}
