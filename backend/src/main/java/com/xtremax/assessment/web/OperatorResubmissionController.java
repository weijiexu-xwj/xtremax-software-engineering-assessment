package com.xtremax.assessment.web;

import com.xtremax.assessment.domain.Application;
import com.xtremax.assessment.domain.FeedbackItem;
import com.xtremax.assessment.repository.FeedbackItemRepository;
import com.xtremax.assessment.service.OperatorResubmissionService;
import com.xtremax.assessment.web.dto.ApplicationReviewDTO;
import com.xtremax.assessment.web.dto.FieldDTO;
import com.xtremax.assessment.web.dto.OperatorResubmissionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class OperatorResubmissionController {
    private final OperatorResubmissionService operatorResubmissionService;
    private final FeedbackItemRepository feedbackItemRepository;

    public OperatorResubmissionController(OperatorResubmissionService operatorResubmissionService,
                                          FeedbackItemRepository feedbackItemRepository) {
        this.operatorResubmissionService = operatorResubmissionService;
        this.feedbackItemRepository = feedbackItemRepository;
    }

    @PostMapping("/{applicationId}/operator-resubmission")
    public ResponseEntity<ApplicationReviewDTO> submitResubmission(@PathVariable UUID applicationId,
                                                                   @RequestBody OperatorResubmissionRequest req) {
        if (req == null || req.fields == null || req.fields.isEmpty()) {
            throw new IllegalArgumentException("At least one updated field is required.");
        }

        Map<String, String> fieldUpdates = req.fields.stream()
                .filter(field -> field != null && field.key != null && !field.key.isBlank())
                .collect(Collectors.toMap(
                        field -> field.key,
                        field -> normalizeFieldValue(field),
                        (left, right) -> right,
                        LinkedHashMap::new
                ));
        if (fieldUpdates.isEmpty()) {
            throw new IllegalArgumentException("At least one valid field update is required.");
        }

        Application updated = operatorResubmissionService.submit(applicationId, req.operatorName, fieldUpdates);
        List<FeedbackItem> feedback = feedbackItemRepository.findByApplicationOrderByCreatedAtAsc(updated);
        return ResponseEntity.ok(ApiMapper.toApplicationReview(updated, feedback));
    }

    private String normalizeFieldValue(FieldDTO field) {
        return field.value == null ? "" : field.value;
    }
}
