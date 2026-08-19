package com.xtremax.assessment.repository;

import com.xtremax.assessment.domain.CommentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentTemplateRepository extends JpaRepository<CommentTemplate, UUID> {
}
