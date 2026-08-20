package com.xtremax.assessment.web.dto;

import java.time.Instant;
import java.util.UUID;

public class FeedbackDTO {
    public UUID id;
    public String targetType;
    public String targetKey;
    public String comment;
    public String status;
    public String resolvedBy;
    public Instant resolvedAt;
    public Instant createdAt;
}
