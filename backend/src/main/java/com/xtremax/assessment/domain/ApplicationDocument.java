package com.xtremax.assessment.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
public class ApplicationDocument {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false)
    private ApplicationRevision revision;

    @NotBlank
    @Column(name = "document_key", nullable = false)
    private String key;

    @NotBlank
    private String filename;

    @OneToOne(cascade = CascadeType.ALL)
    private AIVerificationResult aiResult;

    protected ApplicationDocument() {}

    public ApplicationDocument(ApplicationRevision revision, String key, String filename) {
        this.revision = revision;
        this.key = key;
        this.filename = filename;
    }

    public UUID getId() { return id; }
    public ApplicationRevision getRevision() { return revision; }
    public String getKey() { return key; }
    public String getFilename() { return filename; }
    public AIVerificationResult getAiResult() { return aiResult; }
    public void setAiResult(AIVerificationResult aiResult) { this.aiResult = aiResult; }
}
