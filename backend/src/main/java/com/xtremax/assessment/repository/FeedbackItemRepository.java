package com.xtremax.assessment.repository;

import com.xtremax.assessment.domain.Application;
import com.xtremax.assessment.domain.ApplicationRevision;
import com.xtremax.assessment.domain.FeedbackItem;
import com.xtremax.assessment.domain.FeedbackStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeedbackItemRepository extends JpaRepository<FeedbackItem, UUID> {
    List<FeedbackItem> findByApplicationAndStatus(Application application, FeedbackStatus status);
    List<FeedbackItem> findByRevision(ApplicationRevision revision);
}
