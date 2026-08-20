package com.xtremax.assessment.web.dto;

import java.util.List;

public class RevisionComparisonDTO {
    public List<ComparisonEntryDTO> addedFields;
    public List<ComparisonEntryDTO> removedFields;
    public List<ComparisonEntryDTO> modifiedFields;
    public List<ComparisonEntryDTO> addedDocuments;
    public List<ComparisonEntryDTO> removedDocuments;
    public List<ComparisonEntryDTO> modifiedDocuments;
}
