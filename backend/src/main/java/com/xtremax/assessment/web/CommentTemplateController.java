package com.xtremax.assessment.web;

import com.xtremax.assessment.service.OfficerReviewService;
import com.xtremax.assessment.web.dto.CommentTemplateDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comment-templates")
public class CommentTemplateController {
    private final OfficerReviewService service;

    public CommentTemplateController(OfficerReviewService service) {
        this.service = service;
    }

    @GetMapping
    public List<CommentTemplateDTO> listTemplates() {
        return service.listCommentTemplates().stream().map(com.xtremax.assessment.web.ApiMapper::toCommentTemplateDTO).collect(Collectors.toList());
    }
}
