package com.xtremax.assessment.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.*;

@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(name = "uk_application_revision", columnNames = {"application_id","revisionNumber"}),
    indexes = {@Index(name = "idx_revision_application", columnList = "application_id")}
)
public class ApplicationRevision {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false)
    private Application application;

    @Column(nullable = false)
    private int revisionNumber;

    @NotBlank
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "revision", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ApplicationField> fields = new ArrayList<>();

    @OneToMany(mappedBy = "revision", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ApplicationDocument> documents = new ArrayList<>();

    protected ApplicationRevision() {}

    public ApplicationRevision(Application application, int revisionNumber, String createdBy) {
        this.application = application;
        this.revisionNumber = revisionNumber;
        this.createdBy = createdBy;
    }

    public UUID getId() { return id; }
    public Application getApplication() { return application; }
    public int getRevisionNumber() { return revisionNumber; }
    public String getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public List<ApplicationField> getFields() { return Collections.unmodifiableList(fields); }
    public List<ApplicationDocument> getDocuments() { return Collections.unmodifiableList(documents); }

    public ApplicationField addField(String key, String value) {
        ApplicationField f = new ApplicationField(this, key, value);
        fields.add(f);
        return f;
    }

    public ApplicationDocument addDocument(String key, String filename) {
        ApplicationDocument d = new ApplicationDocument(this, key, filename);
        documents.add(d);
        return d;
    }
}
