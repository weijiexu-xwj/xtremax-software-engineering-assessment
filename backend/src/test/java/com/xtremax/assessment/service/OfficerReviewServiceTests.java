package com.xtremax.assessment.service;

import com.xtremax.assessment.domain.*;
import com.xtremax.assessment.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.RollbackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OfficerReviewServiceTests {
    @Autowired
    private OfficerReviewService service;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationRevisionRepository revisionRepository;

    @Autowired
    private FeedbackItemRepository feedbackItemRepository;

    @Autowired
    private CommentTemplateRepository commentTemplateRepository;

    @Autowired
    private AuditEntryRepository auditEntryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        auditEntryRepository.deleteAll();
        feedbackItemRepository.deleteAll();
        revisionRepository.deleteAll();
        applicationRepository.deleteAll();
        commentTemplateRepository.deleteAll();
    }

    private Application createApplicationInReview(String referenceNumber) {
        Application application = new Application(referenceNumber);
        ApplicationRevision initialRevision = application.createNewRevision("operator");
        initialRevision.addField("name", "Alice");
        application.setCurrentStatus(ApplicationStatus.UNDER_REVIEW);
        applicationRepository.saveAndFlush(application);
        // reload fresh managed entity
        application = applicationRepository.findById(application.getId()).orElseThrow();
        return application;
    }

    @Test
    void successfulFeedbackCreation() {
        Application application = createApplicationInReview("REF-FEEDBACK-1");
        Application applicationReloaded = applicationRepository.findById(application.getId()).orElseThrow();
        var revisions = revisionRepository.findByApplicationOrderByRevisionNumberAsc(applicationReloaded);
        ApplicationRevision revision = revisions.get(0);
        revision.addField("name", "Alice");
        applicationRepository.saveAndFlush(applicationReloaded);

        FeedbackItem created = service.addContextualFeedback(
                application.getId(),
                revision.getId(),
                FeedbackTargetType.FIELD,
                "name",
                "Please provide the full legal name.",
                "officer");

        assertEquals("name", created.getTargetKey());
        assertEquals(FeedbackStatus.OPEN, created.getStatus());
        assertFalse(auditEntryRepository.findByApplicationOrderByTimestampAsc(application).isEmpty());
    }

    @Test
    void invalidFeedbackTarget() {
        Application application = createApplicationInReview("REF-FEEDBACK-2");
        Application applicationReloaded = applicationRepository.findById(application.getId()).orElseThrow();
        var revisions = revisionRepository.findByApplicationOrderByRevisionNumberAsc(applicationReloaded);
        ApplicationRevision revision = revisions.get(0);
        revision.addField("email", "alice@example.com");
        applicationRepository.saveAndFlush(applicationReloaded);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.addContextualFeedback(application.getId(), revision.getId(), FeedbackTargetType.FIELD, "missing", "No comment", "officer"));

        assertTrue(ex.getMessage().contains("does not exist"));
        assertEquals(0, feedbackItemRepository.findAll().size());
    }

    @Test
    void requestingResubmissionWithoutFeedback() {
        Application application = createApplicationInReview("REF-RESUBMIT-1");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.requestPreSiteResubmission(application.getId(), "officer"));

        assertTrue(ex.getMessage().contains("open feedback"));
    }

    @Test
    void successfulRequestForResubmission() {
        Application application = createApplicationInReview("REF-RESUBMIT-2");
        Application applicationReloaded = applicationRepository.findById(application.getId()).orElseThrow();
        var revisions = revisionRepository.findByApplicationOrderByRevisionNumberAsc(applicationReloaded);
        ApplicationRevision revision = revisions.get(0);
        revision.addField("email", "alice@example.com");
        applicationRepository.saveAndFlush(applicationReloaded);

        service.addContextualFeedback(application.getId(), revision.getId(), FeedbackTargetType.FIELD, "email", "Please update email.", "officer");
        Application updated = service.requestPreSiteResubmission(application.getId(), "officer");

        assertEquals(ApplicationStatus.PENDING_PRE_SITE_RESUBMISSION, updated.getCurrentStatus());
        assertEquals(1, feedbackItemRepository.findByApplicationAndStatus(application, FeedbackStatus.OPEN).size());
        assertFalse(notificationRepository.findByApplicationOrderBySentAtAsc(application).isEmpty());
    }

    @Test
    void invalidStatusTransition() {
        Application application = createApplicationInReview("REF-STATUS-1");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.recordOperatorResubmission(application.getId(), "operator"));

        assertTrue(ex.getMessage().contains("Status transition not allowed"));
    }

    @Test
    void statusAuditAndNotificationCreation() {
        Application application = createApplicationInReview("REF-AUDIT-1");
        Application applicationReloaded = applicationRepository.findById(application.getId()).orElseThrow();
        var revisions = revisionRepository.findByApplicationOrderByRevisionNumberAsc(applicationReloaded);
        ApplicationRevision revision = revisions.get(0);
        revision.addField("name", "Alice");
        applicationRepository.saveAndFlush(applicationReloaded);
        service.addContextualFeedback(application.getId(), revision.getId(), FeedbackTargetType.FIELD, "name", "Need full name.", "officer");

        Application updated = service.requestPreSiteResubmission(application.getId(), "officer");
        List<AuditEntry> auditEntries = auditEntryRepository.findByApplicationOrderByTimestampAsc(updated);
        List<Notification> notifications = notificationRepository.findByApplicationOrderBySentAtAsc(updated);

        assertEquals(ApplicationStatus.PENDING_PRE_SITE_RESUBMISSION, updated.getCurrentStatus());
        assertFalse(auditEntries.isEmpty());
        assertFalse(notifications.isEmpty());
    }

    @Test
    void transactionRollbackWhenOnePartFails() {
        Application application = createApplicationInReview("REF-ROLLBACK-1");
        Application applicationReloaded = applicationRepository.findById(application.getId()).orElseThrow();
        var revisions = revisionRepository.findByApplicationOrderByRevisionNumberAsc(applicationReloaded);
        ApplicationRevision revision = revisions.get(0);
        revision.addField("name", "Alice");
        applicationRepository.saveAndFlush(applicationReloaded);
        int auditCountBefore = auditEntryRepository.findByApplicationOrderByTimestampAsc(application).size();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.addContextualFeedback(application.getId(), revision.getId(), FeedbackTargetType.FIELD, "name", "   ", "officer"));

        assertTrue(ex.getMessage().contains("blank"));
        assertEquals(0, feedbackItemRepository.findAll().size());
        assertEquals(auditCountBefore, auditEntryRepository.findByApplicationOrderByTimestampAsc(application).size());
    }

    @Test
    void creationOfANewImmutableRevision() {
        Application application = createApplicationInReview("REF-REVISION-1");
        Application applicationReloaded = applicationRepository.findById(application.getId()).orElseThrow();
        var revisions = revisionRepository.findByApplicationOrderByRevisionNumberAsc(applicationReloaded);
        ApplicationRevision revisionOne = revisions.get(0);
        revisionOne.addField("name", "Alice");
        applicationRepository.saveAndFlush(applicationReloaded);

        service.addContextualFeedback(application.getId(), revisionOne.getId(), FeedbackTargetType.FIELD, "name", "Need full name.", "officer");
        service.requestPreSiteResubmission(application.getId(), "officer");

        ApplicationRevision revisionTwo = service.recordOperatorResubmission(application.getId(), "operator");
        Application reloaded = applicationRepository.findById(application.getId()).orElseThrow();

        List<ApplicationRevision> allRevs = revisionRepository.findByApplicationOrderByRevisionNumberAsc(reloaded);
        assertEquals(2, allRevs.size());
        assertEquals(2, revisionTwo.getRevisionNumber());
        ApplicationRevision loadedRevOne = revisionRepository.findById(revisionOne.getId()).orElseThrow();
        assertTrue(loadedRevOne.isLocked());
        IllegalStateException lockEx = assertThrows(IllegalStateException.class, () -> loadedRevOne.addField("name", "Alice B"));
        assertTrue(lockEx.getMessage().contains("Cannot modify revision"));
    }

    @Test
    void multipleResubmissionRounds() {
        Application application = createApplicationInReview("REF-ROUNDS-1");
        Application applicationReloaded = applicationRepository.findById(application.getId()).orElseThrow();
        var revisions0 = revisionRepository.findByApplicationOrderByRevisionNumberAsc(applicationReloaded);
        ApplicationRevision first = revisions0.get(0);
        first.addField("name", "Alice");
        applicationRepository.saveAndFlush(applicationReloaded);

        service.addContextualFeedback(application.getId(), first.getId(), FeedbackTargetType.FIELD, "name", "Need full name.", "officer");
        service.requestPreSiteResubmission(application.getId(), "officer");
        service.recordOperatorResubmission(application.getId(), "operator");

        Application secondCycleApp = applicationRepository.findById(application.getId()).orElseThrow();
        secondCycleApp.setCurrentStatus(ApplicationStatus.UNDER_REVIEW);
        applicationRepository.saveAndFlush(secondCycleApp);

        var revisions1 = revisionRepository.findByApplicationOrderByRevisionNumberAsc(secondCycleApp);
        ApplicationRevision secondRevision = revisions1.get(1);
        secondRevision.addField("name", "Alice B");
        applicationRepository.saveAndFlush(secondCycleApp);

        service.addContextualFeedback(secondCycleApp.getId(), secondRevision.getId(), FeedbackTargetType.FIELD, "name", "Please confirm legal name.", "officer");
        service.requestPreSiteResubmission(secondCycleApp.getId(), "officer");
        ApplicationRevision thirdRevision = service.recordOperatorResubmission(secondCycleApp.getId(), "operator");

        assertEquals(3, thirdRevision.getRevisionNumber());
        assertEquals(ApplicationStatus.PRE_SITE_RESUBMITTED, applicationRepository.findById(secondCycleApp.getId()).orElseThrow().getCurrentStatus());
    }

    @Test
    void revisionComparison() {
        Application application = createApplicationInReview("REF-COMPARE-1");
        Application applicationReloaded = applicationRepository.findById(application.getId()).orElseThrow();
        var revisions0 = revisionRepository.findByApplicationOrderByRevisionNumberAsc(applicationReloaded);
        ApplicationRevision first = revisions0.get(0);
        first.addField("name", "Alice");
        first.addField("email", "alice@example.com");
        first.addDocument("passport", "passport-old.pdf");
        applicationRepository.saveAndFlush(applicationReloaded);

        // create second revision safely via reload
        Application appForSecond = applicationRepository.findById(application.getId()).orElseThrow();
        ApplicationRevision second = appForSecond.createNewRevision("operator");
        second.addField("name", "Alice B");
        second.addField("phone", "123456");
        second.addDocument("passport", "passport-new.pdf");
        applicationRepository.saveAndFlush(appForSecond);

        OfficerReviewService.RevisionComparisonResult result = service.compareRevisions(application.getId(), 1, 2);

        assertEquals(1, result.getModifiedFields().size());
        assertEquals("name", result.getModifiedFields().get(0).getKey());
        assertEquals(1, result.getAddedFields().size());
        assertEquals("phone", result.getAddedFields().get(0).getKey());
        assertEquals(1, result.getModifiedDocuments().size());
        assertEquals("passport", result.getModifiedDocuments().get(0).getKey());
    }

    @Test
    void successfulFeedbackResolution() {
        Application application = createApplicationInReview("REF-RESOLVE-1");
        Application applicationReloaded = applicationRepository.findById(application.getId()).orElseThrow();
        var revisions = revisionRepository.findByApplicationOrderByRevisionNumberAsc(applicationReloaded);
        ApplicationRevision revision = revisions.get(0);
        revision.addField("email", "alice@example.com");
        applicationRepository.saveAndFlush(applicationReloaded);

        FeedbackItem feedback = service.addContextualFeedback(application.getId(), revision.getId(), FeedbackTargetType.FIELD, "email", "Need a valid email.", "officer");
        FeedbackItem resolved = service.resolveFeedback(feedback.getId(), "officer");

        assertEquals(FeedbackStatus.RESOLVED, resolved.getStatus());
        assertEquals("officer", resolved.getResolvedBy());
        assertNotNull(resolved.getResolvedAt());
    }

    @Test
    void repeatedFeedbackResolution() {
        Application application = createApplicationInReview("REF-RESOLVE-2");
        Application applicationReloaded = applicationRepository.findById(application.getId()).orElseThrow();
        var revisions = revisionRepository.findByApplicationOrderByRevisionNumberAsc(applicationReloaded);
        ApplicationRevision revision = revisions.get(0);
        revision.addField("email", "alice@example.com");
        applicationRepository.saveAndFlush(applicationReloaded);

        FeedbackItem feedback = service.addContextualFeedback(application.getId(), revision.getId(), FeedbackTargetType.FIELD, "email", "Need a valid email.", "officer");
        service.resolveFeedback(feedback.getId(), "officer");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.resolveFeedback(feedback.getId(), "officer"));

        assertTrue(ex.getMessage().contains("already resolved"));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    void optimisticLockConflictWherePractical() {
        Application application = createApplicationInReview("REF-LOCK-1");

        EntityManager em1 = entityManagerFactory.createEntityManager();
        EntityManager em2 = entityManagerFactory.createEntityManager();

        try {
            em1.getTransaction().begin();
            Application first = em1.find(Application.class, application.getId());

            em2.getTransaction().begin();
            Application second = em2.find(Application.class, application.getId());

            first.setCurrentStatus(ApplicationStatus.PENDING_PRE_SITE_RESUBMISSION);
            em1.persist(first);
            em1.getTransaction().commit();

            second.setCurrentStatus(ApplicationStatus.PENDING_APPROVAL);
            em2.persist(second);
            assertThrows(RollbackException.class, () -> em2.getTransaction().commit());
        } finally {
            if (em1.getTransaction().isActive()) em1.getTransaction().rollback();
            if (em2.getTransaction().isActive()) em2.getTransaction().rollback();
            em1.close();
            em2.close();
        }
    }
}

