package com.xtremax.assessment.web.dto;

import java.time.Instant;
import java.util.UUID;

public class AIVerificationDTO {
    public UUID id;
    public boolean passed;
    public String details;
    public Instant checkedAt;
}
