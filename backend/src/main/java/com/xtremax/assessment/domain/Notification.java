package com.xtremax.assessment.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(indexes = {@Index(name = "idx_notification_application", columnList = "application_id")})
public class Notification {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne
    private Application application;

    private String recipient;
    private String message;
    private Instant sentAt = Instant.now();
    private Instant readAt;

    protected Notification() {}

    public Notification(Application application, String recipient, String message) {
        this.application = application;
        this.recipient = recipient;
        this.message = message;
    }

    public UUID getId() { return id; }
    public Application getApplication() { return application; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public Instant getSentAt() { return sentAt; }
    public Instant getReadAt() { return readAt; }
    public void markRead() { this.readAt = Instant.now(); }
}
