package com.xtremax.assessment.service;

import com.xtremax.assessment.domain.Application;
import com.xtremax.assessment.domain.ApplicationDocument;
import com.xtremax.assessment.domain.ApplicationField;
import com.xtremax.assessment.domain.ApplicationRevision;
import com.xtremax.assessment.domain.ApplicationStatus;
import com.xtremax.assessment.domain.AuditEntry;
import com.xtremax.assessment.domain.DomainRules;
import com.xtremax.assessment.domain.Notification;
import com.xtremax.assessment.repository.ApplicationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OperatorResubmissionService {
    private final ApplicationRepository applicationRepository;

    public OperatorResubmissionService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public Application submit(UUID applicationId, String operatorName, Map<String, String> fieldUpdates) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found: " + applicationId));

        if (!DomainRules.isAllowedTransition(application.getCurrentStatus(), ApplicationStatus.PRE_SITE_RESUBMITTED)) {
            throw new IllegalStateException("Status transition not allowed: " + application.getCurrentStatus() + " -> " + ApplicationStatus.PRE_SITE_RESUBMITTED);
        }
        if (fieldUpdates == null || fieldUpdates.isEmpty()) {
            throw new IllegalArgumentException("At least one updated field is required.");
        }

        ApplicationRevision previousRevision = application.getRevisions().isEmpty()
                ? null
                : application.getRevisions().get(application.getRevisions().size() - 1);
        if (previousRevision == null) {
            throw new IllegalStateException("No revision exists for this application.");
        }

        ApplicationRevision newRevision = application.createNewRevision(operatorName == null ? "operator" : operatorName);

        for (ApplicationField field : previousRevision.getFields()) {
            String updatedValue = fieldUpdates.get(field.getKey());
            newRevision.addField(field.getKey(), updatedValue == null ? field.getValue() : updatedValue);
        }

        Set<String> existingKeys = newRevision.getFields().stream()
                .map(ApplicationField::getKey)
                .collect(Collectors.toSet());
        for (Map.Entry<String, String> entry : fieldUpdates.entrySet()) {
            if (!existingKeys.contains(entry.getKey())) {
                newRevision.addField(entry.getKey(), entry.getValue());
            }
        }

        for (ApplicationDocument document : previousRevision.getDocuments()) {
            newRevision.addDocument(document.getKey(), document.getFilename());
        }

        application.changeStatus(
                ApplicationStatus.PRE_SITE_RESUBMITTED,
                operatorName == null ? "operator" : operatorName,
                "Operator resubmitted application"
        );
        application.addNotification(new Notification(
                application,
                "officer",
                "Operator resubmitted revision " + newRevision.getRevisionNumber()
        ));
        application.addAuditEntry(new AuditEntry(
                application,
                operatorName == null ? "operator" : operatorName,
                "OPERATOR_RESUBMISSION",
                "Created revision " + newRevision.getRevisionNumber() + " and set status to PRE_SITE_RESUBMITTED"
        ));

        return applicationRepository.save(application);
    }
}
