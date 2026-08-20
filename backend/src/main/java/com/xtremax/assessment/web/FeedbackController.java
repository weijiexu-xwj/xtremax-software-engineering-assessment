package com.xtremax.assessment.web;

import com.xtremax.assessment.domain.FeedbackItem;
import com.xtremax.assessment.repository.FeedbackItemRepository;
import com.xtremax.assessment.service.OfficerReviewService;
import com.xtremax.assessment.web.dto.FeedbackDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final OfficerReviewService service;
    private final FeedbackItemRepository feedbackItemRepository;

    public FeedbackController(OfficerReviewService service, FeedbackItemRepository feedbackItemRepository) {
        this.service = service;
        this.feedbackItemRepository = feedbackItemRepository;
    }

    @PatchMapping("/{feedbackId}/resolve")
    public ResponseEntity<FeedbackDTO> resolveFeedback(@PathVariable UUID feedbackId, @RequestParam(required = false) String officerName) {
        FeedbackItem updated = service.resolveFeedback(feedbackId, officerName);
        return ResponseEntity.ok(com.xtremax.assessment.web.ApiMapper.toFeedbackDTO(updated));
    }
}
