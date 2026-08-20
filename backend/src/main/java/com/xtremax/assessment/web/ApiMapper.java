package com.xtremax.assessment.web;

import com.xtremax.assessment.domain.*;
import com.xtremax.assessment.service.OfficerReviewService;
import com.xtremax.assessment.web.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public class ApiMapper {
    public static ApplicationReviewDTO toApplicationReview(Application app, List<FeedbackItem> feedback) {
        ApplicationReviewDTO dto = new ApplicationReviewDTO();
        dto.id = app.getId();
        dto.referenceNumber = app.getReferenceNumber();
        dto.officerStatusLabel = app.getCurrentStatus().getOfficerLabel();
        dto.version = app.getVersion();
        dto.createdAt = app.getCreatedAt();
        dto.notifications = app.getNotifications().stream().map(ApiMapper::toNotificationDTO).collect(Collectors.toList());
        dto.auditEntries = app.getAuditEntries().stream().map(ApiMapper::toAuditEntryDTO).collect(Collectors.toList());
        dto.feedback = feedback.stream().map(ApiMapper::toFeedbackDTO).collect(Collectors.toList());
        // latest revision
        if (!app.getRevisions().isEmpty()) {
            ApplicationRevision latest = app.getRevisions().get(app.getRevisions().size()-1);
            dto.latestRevision = toRevisionDTO(latest);
        }
        return dto;
    }

    public static RevisionDTO toRevisionDTO(ApplicationRevision rev) {
        RevisionDTO dto = new RevisionDTO();
        dto.id = rev.getId();
        dto.revisionNumber = rev.getRevisionNumber();
        dto.createdBy = rev.getCreatedBy();
        dto.createdAt = rev.getCreatedAt();
        dto.fields = rev.getFields().stream().map(f -> {
            FieldDTO fd = new FieldDTO(); fd.key = f.getKey(); fd.value = f.getValue(); return fd;
        }).collect(Collectors.toList());
        dto.documents = rev.getDocuments().stream().map(d -> {
            DocumentDTO dd = new DocumentDTO(); dd.id = d.getId(); dd.key = d.getKey(); dd.filename = d.getFilename();
            if (d.getAiResult() != null) {
                AIVerificationDTO a = new AIVerificationDTO(); a.id = d.getAiResult().getId(); a.passed = d.getAiResult().isPassed(); a.details = d.getAiResult().getDetails(); a.checkedAt = d.getAiResult().getCheckedAt(); dd.aiResult = a;
            }
            return dd;
        }).collect(Collectors.toList());
        return dto;
    }

    public static FeedbackDTO toFeedbackDTO(FeedbackItem f) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.id = f.getId();
        dto.targetType = f.getTargetType().name();
        dto.targetKey = f.getTargetKey();
        dto.comment = f.getComment();
        dto.status = f.getStatus().name();
        dto.resolvedBy = f.getResolvedBy();
        dto.resolvedAt = f.getResolvedAt();
        dto.createdAt = f.getCreatedAt();
        return dto;
    }

    public static CommentTemplateDTO toCommentTemplateDTO(CommentTemplate t) {
        CommentTemplateDTO dto = new CommentTemplateDTO();
            dto.id = t.getId(); dto.title = t.getTitle(); dto.text = t.getTemplateText();
        return dto;
    }

    public static AuditEntryDTO toAuditEntryDTO(AuditEntry a) {
        AuditEntryDTO dto = new AuditEntryDTO();
        dto.id = a.getId(); dto.actor = a.getActor(); dto.action = a.getAction(); dto.details = a.getDetails(); dto.timestamp = a.getTimestamp();
        return dto;
    }

    public static NotificationDTO toNotificationDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO(); dto.id = n.getId(); dto.recipient = n.getRecipient(); dto.message = n.getMessage(); dto.sentAt = n.getSentAt(); return dto;
    }

    public static RevisionComparisonDTO toRevisionComparisonDTO(OfficerReviewService.RevisionComparisonResult r) {
        RevisionComparisonDTO dto = new RevisionComparisonDTO();
        dto.addedFields = r.getAddedFields().stream().map(e -> mapComp(e)).collect(Collectors.toList());
        dto.removedFields = r.getRemovedFields().stream().map(e -> mapComp(e)).collect(Collectors.toList());
        dto.modifiedFields = r.getModifiedFields().stream().map(e -> mapComp(e)).collect(Collectors.toList());
        dto.addedDocuments = r.getAddedDocuments().stream().map(e -> mapComp(e)).collect(Collectors.toList());
        dto.removedDocuments = r.getRemovedDocuments().stream().map(e -> mapComp(e)).collect(Collectors.toList());
        dto.modifiedDocuments = r.getModifiedDocuments().stream().map(e -> mapComp(e)).collect(Collectors.toList());
        return dto;
    }

    private static ComparisonEntryDTO mapComp(OfficerReviewService.ComparisonEntry e) {
        ComparisonEntryDTO d = new ComparisonEntryDTO(); d.key = e.getKey(); d.previousValue = e.getPreviousValue(); d.currentValue = e.getCurrentValue(); return d;
    }
}
