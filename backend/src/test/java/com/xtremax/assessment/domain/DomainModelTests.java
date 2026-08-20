package com.xtremax.assessment.domain;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DomainModelTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void officerAndOperatorLabels() {
        assertEquals("Application Received", ApplicationStatus.APPLICATION_RECEIVED.getOfficerLabel());
        assertEquals("Submitted", ApplicationStatus.APPLICATION_RECEIVED.getOperatorLabel());
        assertEquals("Pending Site Visit", ApplicationStatus.SITE_VISIT_SCHEDULED.getOperatorLabel());
    }

    @Test
    public void allowedStatusTransitions() {
        assertTrue(DomainRules.isAllowedTransition(ApplicationStatus.APPLICATION_RECEIVED, ApplicationStatus.UNDER_REVIEW));
        assertTrue(DomainRules.isAllowedTransition(ApplicationStatus.UNDER_REVIEW, ApplicationStatus.PENDING_PRE_SITE_RESUBMISSION));
        assertTrue(DomainRules.isAllowedTransition(ApplicationStatus.PENDING_APPROVAL, ApplicationStatus.APPROVED));
    }

    @Test
    public void rejectedStatusTransitions() {
        assertFalse(DomainRules.isAllowedTransition(ApplicationStatus.APPROVED, ApplicationStatus.UNDER_REVIEW));
        assertFalse(DomainRules.isAllowedTransition(ApplicationStatus.REJECTED, ApplicationStatus.POST_SITE_CLARIFICATION_RESUBMITTED));
    }

    @Test
    public void feedbackStatusEnum() {
        FeedbackItem f = new FeedbackItem(FeedbackTargetType.FIELD, "email", "Please correct");
        assertEquals(FeedbackStatus.OPEN, f.getStatus());
        f.setStatus(FeedbackStatus.ADDRESSED);
        assertEquals(FeedbackStatus.ADDRESSED, f.getStatus());
    }

    @Test
    public void basicDomainValidation() {
        Application a = new Application("");
        var violations = validator.validate(a);
        assertFalse(violations.isEmpty());

        Application good = new Application("REF-001");
        violations = validator.validate(good);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void statusChangeCreatesAuditAndNotification() {
        Application a = new Application("REF-STATUS");
        Notification notification = a.changeStatus(ApplicationStatus.UNDER_REVIEW, "officer", "Please complete the site details");

        assertEquals(ApplicationStatus.UNDER_REVIEW, a.getCurrentStatus());
        assertEquals(1, a.getNotifications().size());
        assertEquals("Please complete the site details", notification.getMessage());
        assertEquals(1, a.getAuditEntries().size());
        assertEquals("APPLICATION_STATUS_CHANGED", a.getAuditEntries().get(0).getAction());
    }

    @Test
    public void revisionsAreSequentialAndImmutable() {
        Application a = new Application("REF-SEQ");
        var r1 = a.createNewRevision("alice");
        r1.addField("name", "Alice");
        var r2 = a.createNewRevision("bob");
        r2.addField("name", "Alice B");

        assertEquals(1, r1.getRevisionNumber());
        assertEquals(2, r2.getRevisionNumber());
        assertEquals(2, a.getRevisions().size());
        assertTrue(r1.isLocked());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> r1.addField("name", "Changed"));
        assertTrue(ex.getMessage().contains("Cannot modify revision 1"));
        assertEquals("Alice", r1.getFields().get(0).getValue());
    }
}
