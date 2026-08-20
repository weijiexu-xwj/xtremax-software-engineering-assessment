package com.xtremax.assessment.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.*;

@Entity
@Table(indexes = {@Index(name = "idx_application_status", columnList = "currentStatus")})
public class Application {
    @Id
    private UUID id = UUID.randomUUID();

    @NotBlank
    @Column(nullable = false, unique = true)
    private String referenceNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ApplicationStatus currentStatus = ApplicationStatus.APPLICATION_RECEIVED;

    @Version
    private Long version;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("revisionNumber ASC")
    private final List<ApplicationRevision> revisions = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AuditEntry> auditEntries = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Notification> notifications = new ArrayList<>();

    private Instant createdAt = Instant.now();

    public Application() {}

    public Application(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public UUID getId() { return id; }
    public String getReferenceNumber() { return referenceNumber; }
    public ApplicationStatus getCurrentStatus() { return currentStatus; }
    public Long getVersion() { return version; }
    public List<ApplicationRevision> getRevisions() { return Collections.unmodifiableList(revisions); }
    public List<AuditEntry> getAuditEntries() { return Collections.unmodifiableList(auditEntries); }
    public List<Notification> getNotifications() { return Collections.unmodifiableList(notifications); }
    public Instant getCreatedAt() { return createdAt; }

    public void addNotification(Notification notification) { this.notifications.add(notification); }
    public void addAuditEntry(AuditEntry auditEntry) { this.auditEntries.add(auditEntry); }

    public ApplicationRevision createNewRevision(String createdBy) {
        int next = revisions.stream().mapToInt(ApplicationRevision::getRevisionNumber).max().orElse(0) + 1;
        revisions.forEach(ApplicationRevision::lock);
        ApplicationRevision rev = new ApplicationRevision(this, next, createdBy);
        this.revisions.add(rev);
        return rev;
    }

    public Notification changeStatus(ApplicationStatus newStatus, String actor, String message) {
        if (!DomainRules.isAllowedTransition(this.currentStatus, newStatus)) {
            throw new IllegalStateException("Status transition not allowed: " + this.currentStatus + " -> " + newStatus);
        }
        ApplicationStatus previous = this.currentStatus;
        this.currentStatus = newStatus;

        String finalMessage = message == null || message.isBlank()
                ? "Status changed from " + previous.getOfficerLabel() + " to " + newStatus.getOfficerLabel()
                : message;

        Notification notification = new Notification(this, actor == null ? "operator" : actor, finalMessage);
        notifications.add(notification);
        auditEntries.add(new AuditEntry(this, actor == null ? "system" : actor, "APPLICATION_STATUS_CHANGED", previous + " -> " + newStatus));
        return notification;
    }

    public void setCurrentStatus(ApplicationStatus newStatus) {
        changeStatus(newStatus, "system", null);
    }
}
