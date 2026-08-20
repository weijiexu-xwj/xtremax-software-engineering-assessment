INSERT INTO comment_template (id, title, template_text)
SELECT '2d4d1db7-2e8c-4f5d-bf86-0b5d9a33d001', 'Missing photo', 'Please upload a clear photo of the primary ID.'
WHERE NOT EXISTS (SELECT 1 FROM comment_template WHERE id = '2d4d1db7-2e8c-4f5d-bf86-0b5d9a33d001');

INSERT INTO comment_template (id, title, template_text)
SELECT '2d4d1db7-2e8c-4f5d-bf86-0b5d9a33d002', 'Incomplete address', 'Please provide the full postal address including postal code.'
WHERE NOT EXISTS (SELECT 1 FROM comment_template WHERE id = '2d4d1db7-2e8c-4f5d-bf86-0b5d9a33d002');

INSERT INTO comment_template (id, title, template_text)
SELECT '2d4d1db7-2e8c-4f5d-bf86-0b5d9a33d003', 'Invalid document', 'The uploaded document appears invalid. Please re-upload a readable copy.'
WHERE NOT EXISTS (SELECT 1 FROM comment_template WHERE id = '2d4d1db7-2e8c-4f5d-bf86-0b5d9a33d003');

INSERT INTO comment_template (id, title, template_text)
SELECT '2d4d1db7-2e8c-4f5d-bf86-0b5d9a33d004', 'Clarify ownership', 'Please clarify whether the applicant owns or rents the premises.'
WHERE NOT EXISTS (SELECT 1 FROM comment_template WHERE id = '2d4d1db7-2e8c-4f5d-bf86-0b5d9a33d004');

INSERT INTO comment_template (id, title, template_text)
SELECT '2d4d1db7-2e8c-4f5d-bf86-0b5d9a33d005', 'Provide consent', 'Please provide signed consent form for third-party checks.'
WHERE NOT EXISTS (SELECT 1 FROM comment_template WHERE id = '2d4d1db7-2e8c-4f5d-bf86-0b5d9a33d005');

INSERT INTO application (id, reference_number, current_status, version, created_at)
SELECT '11111111-1111-1111-1111-111111111111', 'REF-LOCAL-1', 'APPLICATION_RECEIVED', 0, '2026-08-20T09:00:00Z'
WHERE NOT EXISTS (SELECT 1 FROM application WHERE id = '11111111-1111-1111-1111-111111111111');

INSERT INTO application (id, reference_number, current_status, version, created_at)
SELECT '22222222-2222-2222-2222-222222222222', 'REF-LOCAL-2', 'UNDER_REVIEW', 0, '2026-08-20T09:02:00Z'
WHERE NOT EXISTS (SELECT 1 FROM application WHERE id = '22222222-2222-2222-2222-222222222222');

INSERT INTO application (id, reference_number, current_status, version, created_at)
SELECT '33333333-3333-3333-3333-333333333333', 'REF-LOCAL-3', 'PENDING_PRE_SITE_RESUBMISSION', 0, '2026-08-20T09:04:00Z'
WHERE NOT EXISTS (SELECT 1 FROM application WHERE id = '33333333-3333-3333-3333-333333333333');

INSERT INTO application (id, reference_number, current_status, version, created_at)
SELECT '44444444-4444-4444-4444-444444444444', 'REF-LOCAL-4', 'SITE_VISIT_SCHEDULED', 0, '2026-08-20T09:06:00Z'
WHERE NOT EXISTS (SELECT 1 FROM application WHERE id = '44444444-4444-4444-4444-444444444444');

INSERT INTO application (id, reference_number, current_status, version, created_at)
SELECT '55555555-5555-5555-5555-555555555555', 'REF-LOCAL-5', 'PENDING_APPROVAL', 0, '2026-08-20T09:08:00Z'
WHERE NOT EXISTS (SELECT 1 FROM application WHERE id = '55555555-5555-5555-5555-555555555555');

INSERT INTO application_revision (id, application_id, revision_number, created_by, created_at, locked)
SELECT 'a1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 1, 'operator', '2026-08-20T09:00:30Z', false
WHERE NOT EXISTS (SELECT 1 FROM application_revision WHERE id = 'a1111111-1111-1111-1111-111111111111');

INSERT INTO application_revision (id, application_id, revision_number, created_by, created_at, locked)
SELECT 'a2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 1, 'operator', '2026-08-20T09:02:30Z', false
WHERE NOT EXISTS (SELECT 1 FROM application_revision WHERE id = 'a2222222-2222-2222-2222-222222222222');

INSERT INTO application_revision (id, application_id, revision_number, created_by, created_at, locked)
SELECT 'a3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 1, 'operator', '2026-08-20T09:04:30Z', false
WHERE NOT EXISTS (SELECT 1 FROM application_revision WHERE id = 'a3333333-3333-3333-3333-333333333333');

INSERT INTO application_revision (id, application_id, revision_number, created_by, created_at, locked)
SELECT 'a4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', 1, 'operator', '2026-08-20T09:06:30Z', false
WHERE NOT EXISTS (SELECT 1 FROM application_revision WHERE id = 'a4444444-4444-4444-4444-444444444444');

INSERT INTO application_revision (id, application_id, revision_number, created_by, created_at, locked)
SELECT 'a5555555-5555-5555-5555-555555555555', '55555555-5555-5555-5555-555555555555', 1, 'operator', '2026-08-20T09:08:30Z', false
WHERE NOT EXISTS (SELECT 1 FROM application_revision WHERE id = 'a5555555-5555-5555-5555-555555555555');

INSERT INTO application_field (id, revision_id, field_key, field_value)
SELECT 'f1111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', 'name', 'Citizen One'
WHERE NOT EXISTS (SELECT 1 FROM application_field WHERE id = 'f1111111-1111-1111-1111-111111111111');

INSERT INTO application_field (id, revision_id, field_key, field_value)
SELECT 'f2222222-2222-2222-2222-222222222222', 'a2222222-2222-2222-2222-222222222222', 'full_name', 'Citizen Two'
WHERE NOT EXISTS (SELECT 1 FROM application_field WHERE id = 'f2222222-2222-2222-2222-222222222222');

INSERT INTO application_field (id, revision_id, field_key, field_value)
SELECT 'f3333333-3333-3333-3333-333333333333', 'a3333333-3333-3333-3333-333333333333', 'name', 'Citizen Three'
WHERE NOT EXISTS (SELECT 1 FROM application_field WHERE id = 'f3333333-3333-3333-3333-333333333333');

INSERT INTO application_field (id, revision_id, field_key, field_value)
SELECT 'f4444444-4444-4444-4444-444444444444', 'a4444444-4444-4444-4444-444444444444', 'name', 'Citizen Four'
WHERE NOT EXISTS (SELECT 1 FROM application_field WHERE id = 'f4444444-4444-4444-4444-444444444444');

INSERT INTO application_field (id, revision_id, field_key, field_value)
SELECT 'f5555555-5555-5555-5555-555555555555', 'a5555555-5555-5555-5555-555555555555', 'name', 'Citizen Five'
WHERE NOT EXISTS (SELECT 1 FROM application_field WHERE id = 'f5555555-5555-5555-5555-555555555555');

INSERT INTO aiverification_result (id, passed, details, checked_at)
SELECT '91111111-1111-1111-1111-111111111111', TRUE, NULL, '2026-08-20T09:00:45Z'
WHERE NOT EXISTS (SELECT 1 FROM aiverification_result WHERE id = '91111111-1111-1111-1111-111111111111');

INSERT INTO aiverification_result (id, passed, details, checked_at)
SELECT '92222222-2222-2222-2222-222222222222', TRUE, NULL, '2026-08-20T09:02:45Z'
WHERE NOT EXISTS (SELECT 1 FROM aiverification_result WHERE id = '92222222-2222-2222-2222-222222222222');

INSERT INTO aiverification_result (id, passed, details, checked_at)
SELECT '93333333-3333-3333-3333-333333333333', FALSE, 'Document image is slightly blurred near the signature area.', '2026-08-20T09:04:45Z'
WHERE NOT EXISTS (SELECT 1 FROM aiverification_result WHERE id = '93333333-3333-3333-3333-333333333333');

INSERT INTO aiverification_result (id, passed, details, checked_at)
SELECT '94444444-4444-4444-4444-444444444444', TRUE, NULL, '2026-08-20T09:06:45Z'
WHERE NOT EXISTS (SELECT 1 FROM aiverification_result WHERE id = '94444444-4444-4444-4444-444444444444');

INSERT INTO aiverification_result (id, passed, details, checked_at)
SELECT '95555555-5555-5555-5555-555555555555', FALSE, 'Identity mismatch detected in supporting document.', '2026-08-20T09:08:45Z'
WHERE NOT EXISTS (SELECT 1 FROM aiverification_result WHERE id = '95555555-5555-5555-5555-555555555555');

INSERT INTO application_document (id, revision_id, document_key, filename, ai_result_id)
SELECT 'd1111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', 'passport', 'passport-1.pdf', '91111111-1111-1111-1111-111111111111'
WHERE NOT EXISTS (SELECT 1 FROM application_document WHERE id = 'd1111111-1111-1111-1111-111111111111');

INSERT INTO application_document (id, revision_id, document_key, filename, ai_result_id)
SELECT 'd2222222-2222-2222-2222-222222222222', 'a2222222-2222-2222-2222-222222222222', 'passport', 'passport-2.pdf', '92222222-2222-2222-2222-222222222222'
WHERE NOT EXISTS (SELECT 1 FROM application_document WHERE id = 'd2222222-2222-2222-2222-222222222222');

INSERT INTO application_document (id, revision_id, document_key, filename, ai_result_id)
SELECT 'd3333333-3333-3333-3333-333333333333', 'a3333333-3333-3333-3333-333333333333', 'passport', 'passport-3.pdf', '93333333-3333-3333-3333-333333333333'
WHERE NOT EXISTS (SELECT 1 FROM application_document WHERE id = 'd3333333-3333-3333-3333-333333333333');

INSERT INTO application_document (id, revision_id, document_key, filename, ai_result_id)
SELECT 'd4444444-4444-4444-4444-444444444444', 'a4444444-4444-4444-4444-444444444444', 'passport', 'passport-4.pdf', '94444444-4444-4444-4444-444444444444'
WHERE NOT EXISTS (SELECT 1 FROM application_document WHERE id = 'd4444444-4444-4444-4444-444444444444');

INSERT INTO application_document (id, revision_id, document_key, filename, ai_result_id)
SELECT 'd5555555-5555-5555-5555-555555555555', 'a5555555-5555-5555-5555-555555555555', 'passport', 'passport-5.pdf', '95555555-5555-5555-5555-555555555555'
WHERE NOT EXISTS (SELECT 1 FROM application_document WHERE id = 'd5555555-5555-5555-5555-555555555555');

INSERT INTO feedback_item (id, application_id, revision_id, target_type, target_key, comment, status, resolved_by, resolved_at, created_at, updated_at)
SELECT 'b1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', 'FIELD', 'name', 'Please provide full name as shown on passport.', 'OPEN', NULL, NULL, '2026-08-20T09:01:00Z', '2026-08-20T09:01:00Z'
WHERE NOT EXISTS (SELECT 1 FROM feedback_item WHERE id = 'b1111111-1111-1111-1111-111111111111');

INSERT INTO feedback_item (id, application_id, revision_id, target_type, target_key, comment, status, resolved_by, resolved_at, created_at, updated_at)
SELECT 'b2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'a2222222-2222-2222-2222-222222222222', 'FIELD', 'address', 'Please confirm the current residential address.', 'RESOLVED', 'officer', '2026-08-20T09:03:00Z', '2026-08-20T09:03:00Z', '2026-08-20T09:03:00Z'
WHERE NOT EXISTS (SELECT 1 FROM feedback_item WHERE id = 'b2222222-2222-2222-2222-222222222222');

INSERT INTO feedback_item (id, application_id, revision_id, target_type, target_key, comment, status, resolved_by, resolved_at, created_at, updated_at)
SELECT 'b3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'a3333333-3333-3333-3333-333333333333', 'DOCUMENT', 'passport', 'Please upload a clearer copy of the photograph page.', 'OPEN', NULL, NULL, '2026-08-20T09:05:00Z', '2026-08-20T09:05:00Z'
WHERE NOT EXISTS (SELECT 1 FROM feedback_item WHERE id = 'b3333333-3333-3333-3333-333333333333');

INSERT INTO feedback_item (id, application_id, revision_id, target_type, target_key, comment, status, resolved_by, resolved_at, created_at, updated_at)
SELECT 'b4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', 'a4444444-4444-4444-4444-444444444444', 'FIELD', 'ownership_status', 'Please clarify whether the property is owner-occupied or rented.', 'ADDRESSED', 'officer', '2026-08-20T09:07:00Z', '2026-08-20T09:07:00Z', '2026-08-20T09:07:00Z'
WHERE NOT EXISTS (SELECT 1 FROM feedback_item WHERE id = 'b4444444-4444-4444-4444-444444444444');

INSERT INTO feedback_item (id, application_id, revision_id, target_type, target_key, comment, status, resolved_by, resolved_at, created_at, updated_at)
SELECT 'b5555555-5555-5555-5555-555555555555', '55555555-5555-5555-5555-555555555555', 'a5555555-5555-5555-5555-555555555555', 'DOCUMENT', 'supporting_statement', 'Please provide a signed statement confirming the declared business activity.', 'RESOLVED', 'officer', '2026-08-20T09:09:00Z', '2026-08-20T09:09:00Z', '2026-08-20T09:09:00Z'
WHERE NOT EXISTS (SELECT 1 FROM feedback_item WHERE id = 'b5555555-5555-5555-5555-555555555555');

INSERT INTO audit_entry (id, application_id, timestamp, actor, action, details)
SELECT 'e1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '2026-08-20T09:00:30Z', 'operator', 'CREATED_REVISION', 'Created revision 1'
WHERE NOT EXISTS (SELECT 1 FROM audit_entry WHERE id = 'e1111111-1111-1111-1111-111111111111');

INSERT INTO audit_entry (id, application_id, timestamp, actor, action, details)
SELECT 'e2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', '2026-08-20T09:02:30Z', 'officer', 'APPLICATION_STATUS_CHANGED', 'UNDER_REVIEW -> UNDER_REVIEW'
WHERE NOT EXISTS (SELECT 1 FROM audit_entry WHERE id = 'e2222222-2222-2222-2222-222222222222');

INSERT INTO audit_entry (id, application_id, timestamp, actor, action, details)
SELECT 'e3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', '2026-08-20T09:04:30Z', 'system', 'REJECTED_DOCUMENT', 'Passport image quality below threshold'
WHERE NOT EXISTS (SELECT 1 FROM audit_entry WHERE id = 'e3333333-3333-3333-3333-333333333333');

INSERT INTO audit_entry (id, application_id, timestamp, actor, action, details)
SELECT 'e4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', '2026-08-20T09:06:30Z', 'operator', 'SITE_VISIT_SCHEDULED', 'Scheduled site visit for 2026-08-27'
WHERE NOT EXISTS (SELECT 1 FROM audit_entry WHERE id = 'e4444444-4444-4444-4444-444444444444');

INSERT INTO audit_entry (id, application_id, timestamp, actor, action, details)
SELECT 'e5555555-5555-5555-5555-555555555555', '55555555-5555-5555-5555-555555555555', '2026-08-20T09:08:30Z', 'officer', 'PENDING_APPROVAL', 'Case routed to approval review'
WHERE NOT EXISTS (SELECT 1 FROM audit_entry WHERE id = 'e5555555-5555-5555-5555-555555555555');

INSERT INTO notification (id, application_id, recipient, message, sent_at, read_at)
SELECT 'c1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'officer', 'New revision 1 submitted', '2026-08-20T09:00:40Z', NULL
WHERE NOT EXISTS (SELECT 1 FROM notification WHERE id = 'c1111111-1111-1111-1111-111111111111');

INSERT INTO notification (id, application_id, recipient, message, sent_at, read_at)
SELECT 'c2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'operator', 'Application is under review', '2026-08-20T09:02:40Z', '2026-08-20T09:03:10Z'
WHERE NOT EXISTS (SELECT 1 FROM notification WHERE id = 'c2222222-2222-2222-2222-222222222222');

INSERT INTO notification (id, application_id, recipient, message, sent_at, read_at)
SELECT 'c3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'operator', 'Additional information requested', '2026-08-20T09:04:40Z', NULL
WHERE NOT EXISTS (SELECT 1 FROM notification WHERE id = 'c3333333-3333-3333-3333-333333333333');

INSERT INTO notification (id, application_id, recipient, message, sent_at, read_at)
SELECT 'c4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', 'operator', 'Site visit scheduled', '2026-08-20T09:06:40Z', NULL
WHERE NOT EXISTS (SELECT 1 FROM notification WHERE id = 'c4444444-4444-4444-4444-444444444444');

INSERT INTO notification (id, application_id, recipient, message, sent_at, read_at)
SELECT 'c5555555-5555-5555-5555-555555555555', '55555555-5555-5555-5555-555555555555', 'operator', 'Application moved to approval queue', '2026-08-20T09:08:40Z', NULL
WHERE NOT EXISTS (SELECT 1 FROM notification WHERE id = 'c5555555-5555-5555-5555-555555555555');