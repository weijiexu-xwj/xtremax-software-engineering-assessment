package com.xtremax.assessment.repository;

import com.xtremax.assessment.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DaoLayerTests {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationRevisionRepository revisionRepository;

    @Autowired
    private FeedbackItemRepository feedbackRepository;

    @Autowired
    private CommentTemplateRepository commentTemplateRepository;

    @Autowired
    private AuditEntryRepository auditEntryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EntityManagerFactory emf;

    @Test
    void saveAndRetrieveApplication() {
        Application app = new Application("REF-DAO-1");
        applicationRepository.save(app);

        var found = applicationRepository.findById(app.getId());
        assertTrue(found.isPresent());
        assertEquals("REF-DAO-1", found.get().getReferenceNumber());
    }

    @Test
    void findByReferenceNumber() {
        Application app = new Application("REF-DAO-2");
        applicationRepository.save(app);
        var found = applicationRepository.findByReferenceNumber("REF-DAO-2");
        assertTrue(found.isPresent());
        assertEquals(app.getId(), found.get().getId());
    }

    @Test
    void saveMultipleRevisionsAndOrder() {
        Application app = new Application("REF-DAO-3");
        applicationRepository.save(app);

        var r1 = new ApplicationRevision(app, 1, "alice");
        r1.addField("f1", "v1");
        var r2 = new ApplicationRevision(app, 2, "bob");
        r2.addField("f1", "v2");

        revisionRepository.save(r1);
        revisionRepository.save(r2);

        List<ApplicationRevision> revs = revisionRepository.findByApplicationOrderByRevisionNumberAsc(app);
        assertEquals(2, revs.size());
        assertEquals(1, revs.get(0).getRevisionNumber());
        assertEquals(2, revs.get(1).getRevisionNumber());
    }

    @Test
    void uniqueRevisionNumberEnforced() {
        Application app = new Application("REF-DAO-4");
        applicationRepository.save(app);
        var r1 = new ApplicationRevision(app, 1, "alice");
        revisionRepository.save(r1);

        var dup = new ApplicationRevision(app, 1, "eve");
        assertThrows(DataIntegrityViolationException.class, () -> {
            revisionRepository.saveAndFlush(dup);
        });
    }

    @Test
    void retrieveOpenFeedbackForApplicationAndRevision() {
        Application app = new Application("REF-DAO-5");
        applicationRepository.save(app);
        var r1 = new ApplicationRevision(app, 1, "alice");
        revisionRepository.save(r1);

        var f1 = new FeedbackItem(app, r1, FeedbackTargetType.FIELD, "email", "Fix email");
        var f2 = new FeedbackItem(app, r1, FeedbackTargetType.DOCUMENT, "doc1", "Bad doc");
        f2.setStatus(FeedbackStatus.ADDRESSED);
        feedbackRepository.save(f1);
        feedbackRepository.save(f2);

        var open = feedbackRepository.findByApplicationAndStatus(app, FeedbackStatus.OPEN);
        assertEquals(1, open.size());

        var forRev = feedbackRepository.findByRevision(r1);
        assertEquals(2, forRev.size());
    }

    @Test
    void retrieveCommentTemplates() {
        commentTemplateRepository.save(new CommentTemplate("t1", "Please update"));
        commentTemplateRepository.save(new CommentTemplate("t2", "Missing document"));
        var all = commentTemplateRepository.findAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    void retrieveAuditEntriesInOrder() throws InterruptedException {
        Application app = new Application("REF-DAO-6");
        applicationRepository.save(app);

        var a1 = new AuditEntry(app, "alice", "create", "created");
        Thread.sleep(5);
        var a2 = new AuditEntry(app, "bob", "update", "updated");
        auditEntryRepository.save(a1);
        auditEntryRepository.save(a2);

        var entries = auditEntryRepository.findByApplicationOrderByTimestampAsc(app);
        assertEquals(2, entries.size());
        assertTrue(entries.get(0).getTimestamp().isBefore(entries.get(1).getTimestamp()) || entries.get(0).getTimestamp().equals(entries.get(1).getTimestamp()));
    }

    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    @Test
    void optimisticLockBehaviour() {
        Application app = new Application("REF-DAO-7");
        applicationRepository.saveAndFlush(app);

        // Use two separate EntityManagers to simulate concurrent updates
        EntityManager em1 = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();

        try {
            em1.getTransaction().begin();
            var a1 = em1.find(Application.class, app.getId());

            em2.getTransaction().begin();
            var a2 = em2.find(Application.class, app.getId());

            // commit change in em1
            a1.setCurrentStatus(ApplicationStatus.UNDER_REVIEW);
            em1.persist(a1);
            em1.getTransaction().commit();

            // now try to commit change in em2 - should fail due to optimistic lock
            // use the same allowed transition so the business rule permits it, optimistic lock should still fail
            a2.setCurrentStatus(ApplicationStatus.UNDER_REVIEW);
            em2.persist(a2);
            var ex = assertThrows(jakarta.persistence.RollbackException.class, () -> em2.getTransaction().commit());
            // commit may wrap the optimistic lock in a RollbackException; ensure cause contains OptimisticLockException
            assertTrue(ex.getCause() instanceof OptimisticLockException || (ex.getCause() != null && ex.getCause().getCause() instanceof OptimisticLockException));
        } finally {
            if (em1.getTransaction().isActive()) em1.getTransaction().rollback();
            if (em2.getTransaction().isActive()) em2.getTransaction().rollback();
            em1.close();
            em2.close();
            // cleanup committed test data
            try { applicationRepository.deleteById(app.getId()); } catch (Exception ignore) {}
        }
    }
}
