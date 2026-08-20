package com.xtremax.assessment.web.dto;

import java.time.Instant;
import java.util.UUID;

public class AuditEntryDTO {
    public UUID id;
    public String actor;
    public String action;
    public String details;
    public Instant timestamp;
}
