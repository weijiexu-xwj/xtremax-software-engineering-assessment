package com.xtremax.assessment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class CommentTemplate {
    @Id
    private UUID id = UUID.randomUUID();

    private String title;
    private String templateText;

    protected CommentTemplate() {}

    public CommentTemplate(String title, String templateText) {
        this.title = title;
        this.templateText = templateText;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getTemplateText() { return templateText; }
}
