package com.xtremax.assessment.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(indexes = {@Index(name = "idx_audit_application_ts", columnList = "application_id,timestamp")})
public class AuditEntry {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne
    private Application application;

    private Instant timestamp = Instant.now();
    private String actor;
    private String action;
    private String details;

    protected AuditEntry() {}

    public AuditEntry(Application application, String actor, String action, String details) {
        this.application = application;
        this.actor = actor;
        this.action = action;
        this.details = details;
    }

    public UUID getId() { return id; }
    public Instant getTimestamp() { return timestamp; }
    public String getActor() { return actor; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public Application getApplication() { return application; }
}
