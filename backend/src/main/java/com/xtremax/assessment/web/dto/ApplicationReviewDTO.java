package com.xtremax.assessment.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ApplicationReviewDTO {
    public UUID id;
    public String referenceNumber;
    public String officerStatusLabel;
    public Long version;
    public RevisionDTO latestRevision;
    public List<FeedbackDTO> feedback;
    public List<AuditEntryDTO> auditEntries;
    public List<NotificationDTO> notifications;
    public Instant createdAt;
}
