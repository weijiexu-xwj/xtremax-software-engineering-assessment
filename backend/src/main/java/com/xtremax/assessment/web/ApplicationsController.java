package com.xtremax.assessment.web;

import com.xtremax.assessment.domain.Application;
import com.xtremax.assessment.domain.FeedbackItem;
import com.xtremax.assessment.repository.*;
import com.xtremax.assessment.service.OfficerReviewService;
import com.xtremax.assessment.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class ApplicationsController {
    private final OfficerReviewService service;
    private final FeedbackItemRepository feedbackRepo;
    private final ApplicationRevisionRepository revisionRepo;
    private final CommentTemplateRepository templateRepo;

    public ApplicationsController(OfficerReviewService service,
                                  FeedbackItemRepository feedbackRepo,
                                  ApplicationRevisionRepository revisionRepo,
                                  CommentTemplateRepository templateRepo) {
        this.service = service;
        this.feedbackRepo = feedbackRepo;
        this.revisionRepo = revisionRepo;
        this.templateRepo = templateRepo;
    }

    @GetMapping("/{applicationId}/review")
    public ApplicationReviewDTO getApplicationReview(@PathVariable UUID applicationId) {
        return service.getApplicationReviewDTO(applicationId);
    }

    @GetMapping("/{applicationId}/revisions")
    public List<RevisionDTO> listRevisions(@PathVariable UUID applicationId) {
        return service.listRevisionsDTO(applicationId);
    }

    @GetMapping("/{applicationId}/revisions/compare")
    public RevisionComparisonDTO compareRevisions(@PathVariable UUID applicationId,
                                                  @RequestParam(name = "from") int from,
                                                  @RequestParam(name = "to") int to) {
        OfficerReviewService.RevisionComparisonResult result = service.compareRevisions(applicationId, from, to);
        return ApiMapper.toRevisionComparisonDTO(result);
    }

    @PostMapping("/{applicationId}/feedback")
    public ResponseEntity<FeedbackDTO> addFeedback(@PathVariable UUID applicationId, @RequestBody CreateFeedbackRequest req) {
        // validate
        if (req == null || req.comment == null || req.comment.isBlank()) {
            throw new IllegalArgumentException("comment is required");
        }
        com.xtremax.assessment.domain.FeedbackTargetType targetType = com.xtremax.assessment.domain.FeedbackTargetType.valueOf(req.targetType);
        java.util.UUID revisionId = req.revisionId != null ? req.revisionId : service.getLatestRevisionId(applicationId);
        FeedbackItem created = service.addContextualFeedback(applicationId, revisionId,
                targetType, req.targetKey, req.comment, req.officerName);
        return new ResponseEntity<>(ApiMapper.toFeedbackDTO(created), HttpStatus.CREATED);
    }

    @PostMapping("/{applicationId}/request-information")
    public ResponseEntity<ApplicationReviewDTO> requestInformation(@PathVariable UUID applicationId, @RequestBody RequestInformationRequest req) {
        Application updated = service.requestPreSiteResubmission(applicationId, req == null ? null : req.officerName);
        List<FeedbackItem> feedback = feedbackRepo.findAll().stream().filter(f -> f.getApplication().getId().equals(updated.getId())).collect(Collectors.toList());
        return ResponseEntity.ok(ApiMapper.toApplicationReview(updated, feedback));
    }

    @GetMapping("/{applicationId}/audit")
    public List<AuditEntryDTO> getAudit(@PathVariable UUID applicationId) {
        return service.getApplicationAuditHistory(applicationId).stream().map(ApiMapper::toAuditEntryDTO).collect(Collectors.toList());
    }

    @GetMapping("/{applicationId}/notifications")
    public List<NotificationDTO> getNotifications(@PathVariable UUID applicationId) {
        return service.getApplicationNotifications(applicationId).stream().map(ApiMapper::toNotificationDTO).collect(Collectors.toList());
    }

    private UUID findRevisionIdForLatest(UUID applicationId) {
        Application app = service.getApplicationForOfficerReview(applicationId);
        if (app.getRevisions().isEmpty()) throw new EntityNotFoundException("No revisions for application: " + applicationId);
        return app.getRevisions().get(app.getRevisions().size()-1).getId();
    }
}
