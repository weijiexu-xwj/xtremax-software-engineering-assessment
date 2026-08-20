package com.xtremax.assessment.web.dto;

import java.util.UUID;

public class ApplicationListItemDTO {
    public final UUID id;
    public final String referenceNumber;

    public ApplicationListItemDTO(UUID id, String referenceNumber) {
        this.id = id;
        this.referenceNumber = referenceNumber;
    }
}
