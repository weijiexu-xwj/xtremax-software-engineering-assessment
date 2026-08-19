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
    public Instant getCreatedAt() { return createdAt; }

    public ApplicationRevision createNewRevision(String createdBy) {
        int next = revisions.stream().mapToInt(ApplicationRevision::getRevisionNumber).max().orElse(0) + 1;
        ApplicationRevision rev = new ApplicationRevision(this, next, createdBy);
        this.revisions.add(rev);
        return rev;
    }

    public void setCurrentStatus(ApplicationStatus newStatus) {
        if (!DomainRules.isAllowedTransition(this.currentStatus, newStatus)) {
            throw new IllegalStateException("Status transition not allowed: " + this.currentStatus + " -> " + newStatus);
        }
        this.currentStatus = newStatus;
    }
}
