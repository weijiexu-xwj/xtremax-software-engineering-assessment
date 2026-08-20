CREATE TABLE IF NOT EXISTS comment_template (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    template_text VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS application (
    id UUID PRIMARY KEY,
    reference_number VARCHAR(255) NOT NULL UNIQUE,
    current_status VARCHAR(50) NOT NULL,
    version BIGINT,
    created_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_application_status ON application (current_status);

CREATE TABLE IF NOT EXISTS application_revision (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL,
    revision_number INTEGER NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_application_revision_application FOREIGN KEY (application_id) REFERENCES application (id),
    CONSTRAINT uk_application_revision UNIQUE (application_id, revision_number)
);

CREATE INDEX IF NOT EXISTS idx_revision_application ON application_revision (application_id);

CREATE TABLE IF NOT EXISTS application_field (
    id UUID PRIMARY KEY,
    revision_id UUID NOT NULL,
    field_key VARCHAR(255) NOT NULL,
    field_value VARCHAR(255),
    CONSTRAINT fk_application_field_revision FOREIGN KEY (revision_id) REFERENCES application_revision (id)
);

CREATE TABLE IF NOT EXISTS aiverification_result (
    id UUID PRIMARY KEY,
    passed BOOLEAN NOT NULL,
    details VARCHAR(255),
    checked_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS application_document (
    id UUID PRIMARY KEY,
    revision_id UUID NOT NULL,
    document_key VARCHAR(255) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    ai_result_id UUID,
    CONSTRAINT fk_application_document_revision FOREIGN KEY (revision_id) REFERENCES application_revision (id),
    CONSTRAINT fk_application_document_ai_result FOREIGN KEY (ai_result_id) REFERENCES aiverification_result (id)
);

CREATE TABLE IF NOT EXISTS feedback_item (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL,
    revision_id UUID,
    target_type VARCHAR(50) NOT NULL,
    target_key VARCHAR(255) NOT NULL,
    comment VARCHAR(255) NOT NULL,
    status VARCHAR(50),
    resolved_by VARCHAR(255),
    resolved_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_feedback_application FOREIGN KEY (application_id) REFERENCES application (id),
    CONSTRAINT fk_feedback_revision FOREIGN KEY (revision_id) REFERENCES application_revision (id)
);

CREATE INDEX IF NOT EXISTS idx_feedback_app_status ON feedback_item (application_id, status);

CREATE TABLE IF NOT EXISTS audit_entry (
    id UUID PRIMARY KEY,
    application_id UUID,
    timestamp TIMESTAMP,
    actor VARCHAR(255),
    action VARCHAR(255),
    details VARCHAR(255),
    CONSTRAINT fk_audit_entry_application FOREIGN KEY (application_id) REFERENCES application (id)
);

CREATE INDEX IF NOT EXISTS idx_audit_application_ts ON audit_entry (application_id, timestamp);

CREATE TABLE IF NOT EXISTS notification (
    id UUID PRIMARY KEY,
    application_id UUID,
    recipient VARCHAR(255),
    message VARCHAR(255),
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    CONSTRAINT fk_notification_application FOREIGN KEY (application_id) REFERENCES application (id)
);

CREATE INDEX IF NOT EXISTS idx_notification_application ON notification (application_id);
