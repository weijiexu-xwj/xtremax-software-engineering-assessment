package com.xtremax.assessment.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class RevisionDTO {
    public UUID id;
    public int revisionNumber;
    public String createdBy;
    public Instant createdAt;
    public List<FieldDTO> fields;
    public List<DocumentDTO> documents;
}
