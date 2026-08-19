package com.xtremax.assessment.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
public class ApplicationField {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false)
    private ApplicationRevision revision;

    @NotBlank
    @Column(name = "field_key", nullable = false)
    private String key;

    @Column(name = "field_value")
    private String value;

    protected ApplicationField() {}

    public ApplicationField(ApplicationRevision revision, String key, String value) {
        this.revision = revision;
        this.key = key;
        this.value = value;
    }

    public UUID getId() { return id; }
    public ApplicationRevision getRevision() { return revision; }
    public String getKey() { return key; }
    public String getValue() { return value; }
}
