package com.xtremax.assessment.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class AIVerificationResult {
    @Id
    private UUID id = UUID.randomUUID();

    private boolean passed;

    private String details;

    private Instant checkedAt = Instant.now();

    public AIVerificationResult() {}

    public AIVerificationResult(boolean passed, String details) {
        this.passed = passed;
        this.details = details;
    }

    public UUID getId() { return id; }
    public boolean isPassed() { return passed; }
    public String getDetails() { return details; }
    public Instant getCheckedAt() { return checkedAt; }
}
