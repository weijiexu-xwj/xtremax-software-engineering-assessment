package com.xtremax.assessment.service;

import com.xtremax.assessment.domain.*;
import com.xtremax.assessment.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OfficerReviewService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationRevisionRepository revisionRepository;
    private final FeedbackItemRepository feedbackItemRepository;
    private final CommentTemplateRepository commentTemplateRepository;
    private final AuditEntryRepository auditEntryRepository;
    private final NotificationRepository notificationRepository;

    public OfficerReviewService(
            ApplicationRepository applicationRepository,
            ApplicationRevisionRepository revisionRepository,
            FeedbackItemRepository feedbackItemRepository,
            CommentTemplateRepository commentTemplateRepository,
            AuditEntryRepository auditEntryRepository,
            NotificationRepository notificationRepository) {
        this.applicationRepository = applicationRepository;
        this.revisionRepository = revisionRepository;
        this.feedbackItemRepository = feedbackItemRepository;
        this.commentTemplateRepository = commentTemplateRepository;
        this.auditEntryRepository = auditEntryRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public Application getApplicationForOfficerReview(UUID applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found: " + applicationId));
    }

    @Transactional
    public FeedbackItem addContextualFeedback(UUID applicationId, UUID revisionId,
                                             FeedbackTargetType targetType,
                                             String targetKey,
                                             String comment,
                                             String officerName) {
        Application application = getApplicationForOfficerReview(applicationId);
        ApplicationRevision revision = revisionRepository.findById(revisionId)
                .orElseThrow(() -> new EntityNotFoundException("Revision not found: " + revisionId));

        if (!application.getId().equals(revision.getApplication().getId())) {
            throw new IllegalArgumentException("Selected revision does not belong to the application.");
        }
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Feedback comment cannot be blank.");
        }
        boolean targetExists = targetType == FeedbackTargetType.FIELD
                ? revision.getFields().stream().anyMatch(field -> field.getKey().equals(targetKey))
                : revision.getDocuments().stream().anyMatch(document -> document.getKey().equals(targetKey));
        if (!targetExists) {
            throw new IllegalArgumentException("Feedback target does not exist in the selected revision.");
        }

        FeedbackItem feedback = new FeedbackItem(application, revision, targetType, targetKey, comment);
        FeedbackItem saved = feedbackItemRepository.save(feedback);
        auditEntryRepository.save(new AuditEntry(
                application,
                officerName == null ? "officer" : officerName,
                "FEEDBACK_CREATED",
                "Added " + targetType + " feedback for target " + targetKey + " on revision " + revision.getRevisionNumber()
        ));
        return saved;
    }

    @Transactional(readOnly = true)
    public List<CommentTemplate> listCommentTemplates() {
        return commentTemplateRepository.findAll();
    }

    @Transactional
    public Application requestPreSiteResubmission(UUID applicationId, String officerName) {
        Application application = getApplicationForOfficerReview(applicationId);
        if (feedbackItemRepository.findByApplicationAndStatus(application, FeedbackStatus.OPEN).isEmpty()) {
            throw new IllegalStateException("At least one open feedback item is required to request pre-site resubmission.");
        }

        application.changeStatus(
                ApplicationStatus.PENDING_PRE_SITE_RESUBMISSION,
                officerName == null ? "officer" : officerName,
                "Requested pre-site resubmission"
        );
        return applicationRepository.save(application);
    }

    @Transactional
    public ApplicationRevision recordOperatorResubmission(UUID applicationId, String operatorName) {
        Application application = getApplicationForOfficerReview(applicationId);
        ApplicationRevision newRevision = application.createNewRevision(operatorName == null ? "operator" : operatorName);

        if (!DomainRules.isAllowedTransition(application.getCurrentStatus(), ApplicationStatus.PRE_SITE_RESUBMITTED)) {
            throw new IllegalStateException("Status transition not allowed: " + application.getCurrentStatus() + " -> " + ApplicationStatus.PRE_SITE_RESUBMITTED);
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

        applicationRepository.save(application);
        return newRevision;
    }

    @Transactional(readOnly = true)
    public RevisionComparisonResult compareRevisions(UUID applicationId, int previousRevisionNumber, int currentRevisionNumber) {
        Application application = getApplicationForOfficerReview(applicationId);
        ApplicationRevision previous = revisionRepository.findByApplicationAndRevisionNumber(application, previousRevisionNumber)
                .orElseThrow(() -> new EntityNotFoundException("Revision not found: " + previousRevisionNumber));
        ApplicationRevision current = revisionRepository.findByApplicationAndRevisionNumber(application, currentRevisionNumber)
                .orElseThrow(() -> new EntityNotFoundException("Revision not found: " + currentRevisionNumber));

        Map<String, String> previousFields = previous.getFields().stream()
                .collect(Collectors.toMap(ApplicationField::getKey, ApplicationField::getValue, (left, right) -> right));
        Map<String, String> currentFields = current.getFields().stream()
                .collect(Collectors.toMap(ApplicationField::getKey, ApplicationField::getValue, (left, right) -> right));

        Set<String> sharedFields = new HashSet<>(previousFields.keySet());
        sharedFields.retainAll(currentFields.keySet());

        List<ComparisonEntry> addedFields = currentFields.entrySet().stream()
                .filter(entry -> !previousFields.containsKey(entry.getKey()))
                .map(entry -> new ComparisonEntry(entry.getKey(), null, entry.getValue()))
                .toList();
        List<ComparisonEntry> removedFields = previousFields.entrySet().stream()
                .filter(entry -> !currentFields.containsKey(entry.getKey()))
                .map(entry -> new ComparisonEntry(entry.getKey(), entry.getValue(), null))
                .toList();
        List<ComparisonEntry> modifiedFields = sharedFields.stream()
                .filter(key -> !Objects.equals(previousFields.get(key), currentFields.get(key)))
                .map(key -> new ComparisonEntry(key, previousFields.get(key), currentFields.get(key)))
                .toList();

        Map<String, String> previousDocuments = previous.getDocuments().stream()
                .collect(Collectors.toMap(ApplicationDocument::getKey, ApplicationDocument::getFilename, (left, right) -> right));
        Map<String, String> currentDocuments = current.getDocuments().stream()
                .collect(Collectors.toMap(ApplicationDocument::getKey, ApplicationDocument::getFilename, (left, right) -> right));

        Set<String> sharedDocuments = new HashSet<>(previousDocuments.keySet());
        sharedDocuments.retainAll(currentDocuments.keySet());

        List<ComparisonEntry> addedDocuments = currentDocuments.entrySet().stream()
                .filter(entry -> !previousDocuments.containsKey(entry.getKey()))
                .map(entry -> new ComparisonEntry(entry.getKey(), null, entry.getValue()))
                .toList();
        List<ComparisonEntry> removedDocuments = previousDocuments.entrySet().stream()
                .filter(entry -> !currentDocuments.containsKey(entry.getKey()))
                .map(entry -> new ComparisonEntry(entry.getKey(), entry.getValue(), null))
                .toList();
        List<ComparisonEntry> modifiedDocuments = sharedDocuments.stream()
                .filter(key -> !Objects.equals(previousDocuments.get(key), currentDocuments.get(key)))
                .map(key -> new ComparisonEntry(key, previousDocuments.get(key), currentDocuments.get(key)))
                .toList();

        return new RevisionComparisonResult(addedFields, removedFields, modifiedFields, addedDocuments, removedDocuments, modifiedDocuments);
    }

    @Transactional
    public FeedbackItem resolveFeedback(UUID feedbackId, String officerName) {
        FeedbackItem feedback = feedbackItemRepository.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found: " + feedbackId));

        if (feedback.getStatus() == FeedbackStatus.RESOLVED || feedback.getStatus() == FeedbackStatus.ADDRESSED || feedback.getStatus() == FeedbackStatus.DISMISSED) {
            throw new IllegalStateException("Feedback is already resolved.");
        }

        feedback.resolve(officerName == null ? "officer" : officerName);
        feedbackItemRepository.save(feedback);
        auditEntryRepository.save(new AuditEntry(
                feedback.getApplication(),
                officerName == null ? "officer" : officerName,
                "FEEDBACK_RESOLVED",
                "Resolved feedback for " + feedback.getTargetType() + " " + feedback.getTargetKey()
        ));
        return feedback;
    }

    @Transactional(readOnly = true)
    public List<AuditEntry> getApplicationAuditHistory(UUID applicationId) {
        Application application = getApplicationForOfficerReview(applicationId);
        return auditEntryRepository.findByApplicationOrderByTimestampAsc(application);
    }

    @Transactional(readOnly = true)
    public List<Notification> getApplicationNotifications(UUID applicationId) {
        Application application = getApplicationForOfficerReview(applicationId);
        return notificationRepository.findByApplicationOrderBySentAtAsc(application);
    }

    public static class RevisionComparisonResult {
        private final List<ComparisonEntry> addedFields;
        private final List<ComparisonEntry> removedFields;
        private final List<ComparisonEntry> modifiedFields;
        private final List<ComparisonEntry> addedDocuments;
        private final List<ComparisonEntry> removedDocuments;
        private final List<ComparisonEntry> modifiedDocuments;

        public RevisionComparisonResult(List<ComparisonEntry> addedFields,
                                       List<ComparisonEntry> removedFields,
                                       List<ComparisonEntry> modifiedFields,
                                       List<ComparisonEntry> addedDocuments,
                                       List<ComparisonEntry> removedDocuments,
                                       List<ComparisonEntry> modifiedDocuments) {
            this.addedFields = addedFields;
            this.removedFields = removedFields;
            this.modifiedFields = modifiedFields;
            this.addedDocuments = addedDocuments;
            this.removedDocuments = removedDocuments;
            this.modifiedDocuments = modifiedDocuments;
        }

        public List<ComparisonEntry> getAddedFields() { return addedFields; }
        public List<ComparisonEntry> getRemovedFields() { return removedFields; }
        public List<ComparisonEntry> getModifiedFields() { return modifiedFields; }
        public List<ComparisonEntry> getAddedDocuments() { return addedDocuments; }
        public List<ComparisonEntry> getRemovedDocuments() { return removedDocuments; }
        public List<ComparisonEntry> getModifiedDocuments() { return modifiedDocuments; }
    }

    public static class ComparisonEntry {
        private final String key;
        private final String previousValue;
        private final String currentValue;

        public ComparisonEntry(String key, String previousValue, String currentValue) {
            this.key = key;
            this.previousValue = previousValue;
            this.currentValue = currentValue;
        }

        public String getKey() { return key; }
        public String getPreviousValue() { return previousValue; }
        public String getCurrentValue() { return currentValue; }
    }
}
