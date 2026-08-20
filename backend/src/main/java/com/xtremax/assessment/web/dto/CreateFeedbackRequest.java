package com.xtremax.assessment.web.dto;

import java.util.UUID;

public class CreateFeedbackRequest {
    public String targetType; // FIELD or DOCUMENT
    public String targetKey;
    public String comment;
    public String officerName;
    public UUID revisionId; // optional: target a specific revision; if null the latest revision will be used
}
