package com.lms.system.notification.issuetracker.repository;

import com.lms.system.notification.issuetracker.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedBackRepository extends JpaRepository<Feedback,Long> {
}
