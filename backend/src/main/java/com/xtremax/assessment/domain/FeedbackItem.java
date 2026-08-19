package com.xtremax.assessment.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(indexes = {@Index(name = "idx_feedback_app_status", columnList = "application_id,status")})
public class FeedbackItem {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false)
    private Application application;

    @ManyToOne
    private ApplicationRevision revision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackTargetType targetType;

    @NotBlank
    @Column(nullable = false)
    private String targetKey;

    @NotBlank
    @Column(nullable = false)
    private String comment;

    @Enumerated(EnumType.STRING)
    private FeedbackStatus status = FeedbackStatus.OPEN;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public FeedbackItem() {}

    public FeedbackItem(Application application, ApplicationRevision revision, FeedbackTargetType targetType, String targetKey, String comment) {
        this.application = application;
        this.revision = revision;
        this.targetType = targetType;
        this.targetKey = targetKey;
        this.comment = comment;
    }

    public FeedbackItem(FeedbackTargetType feedbackTargetType, String email, String pleaseCorrect) {
        this.targetType = feedbackTargetType;
        this.targetKey = email;
        this.comment = pleaseCorrect;
    }

    public UUID getId() { return id; }
    public Application getApplication() { return application; }
    public ApplicationRevision getRevision() { return revision; }
    public FeedbackTargetType getTargetType() { return targetType; }
    public String getTargetKey() { return targetKey; }
    public String getComment() { return comment; }
    public FeedbackStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
