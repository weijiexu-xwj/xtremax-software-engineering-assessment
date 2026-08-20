package com.xtremax.assessment.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtremax.assessment.domain.*;
import com.xtremax.assessment.repository.*;
import com.xtremax.assessment.web.dto.CreateFeedbackRequest;
import com.xtremax.assessment.web.dto.RequestInformationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApplicationsControllerTests {
    @Autowired
    MockMvc mvc;

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    ApplicationRevisionRepository revisionRepository;

    @Autowired
    FeedbackItemRepository feedbackItemRepository;

    @Autowired
    CommentTemplateRepository commentTemplateRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    AuditEntryRepository auditEntryRepository;

    @Autowired
    ObjectMapper objectMapper;

    private Application createApp() {
        Application app = new Application("REF-CTRL-1");
        ApplicationRevision rev = app.createNewRevision("operator");
        rev.addField("name", "Alice");
        rev.addDocument("passport", "p.pdf");
        app.setCurrentStatus(ApplicationStatus.UNDER_REVIEW);
        return applicationRepository.saveAndFlush(app);
    }

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        auditEntryRepository.deleteAll();
        feedbackItemRepository.deleteAll();
        revisionRepository.deleteAll();
        applicationRepository.deleteAll();
        commentTemplateRepository.deleteAll();
    }

    @Test
    void retrievingApplicationReview() throws Exception {
        Application app = createApp();
        mvc.perform(get("/api/applications/" + app.getId() + "/review").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber", is("REF-CTRL-1")))
                .andExpect(jsonPath("$.latestRevision.revisionNumber", is(1)))
                .andExpect(jsonPath("$.latestRevision.fields[0].key", is("name")));
    }

    @Test
    void addingFeedback() throws Exception {
        Application app = createApp();
        CreateFeedbackRequest req = new CreateFeedbackRequest();
        req.targetType = "FIELD"; req.targetKey = "name"; req.comment = "Please supply full name"; req.officerName = "officer";

        mvc.perform(post("/api/applications/" + app.getId() + "/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.targetKey", is("name")));
    }

    @Test
    void invalidFeedbackInput() throws Exception {
        Application app = createApp();
        CreateFeedbackRequest req = new CreateFeedbackRequest();
        req.targetType = "FIELD"; req.targetKey = "missing"; req.comment = ""; req.officerName = "officer";

        mvc.perform(post("/api/applications/" + app.getId() + "/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestingMoreInformation_and_invalidTransition() throws Exception {
        Application app = createApp();
        // invalid because no open feedback
        RequestInformationRequest req = new RequestInformationRequest(); req.officerName = "officer"; req.message = "Please update";
        mvc.perform(post("/api/applications/" + app.getId() + "/request-information")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());

        // add feedback then request
        FeedbackItem f = new FeedbackItem(app, app.getRevisions().get(0), FeedbackTargetType.FIELD, "name", "Please update");
        feedbackItemRepository.saveAndFlush(f);
        mvc.perform(post("/api/applications/" + app.getId() + "/request-information")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.officerStatusLabel", containsString("Pending Pre-Site Resubmission")));
    }

    @Test
    void comparingRevisions() throws Exception {
        Application app = createApp();
        // create second revision
        Application saved = applicationRepository.findById(app.getId()).orElseThrow();
        ApplicationRevision second = saved.createNewRevision("operator");
        second.addField("name","Alice B");
        second.addDocument("passport","p2.pdf");
        applicationRepository.saveAndFlush(saved);

        mvc.perform(get("/api/applications/" + app.getId() + "/revisions/compare?from=1&to=2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modifiedFields", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.modifiedDocuments", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void resolvingFeedback_and_notFound_and_conflict() throws Exception {
        Application app = createApp();
        FeedbackItem f = new FeedbackItem(app, app.getRevisions().get(0), FeedbackTargetType.FIELD, "name", "Please update");
        feedbackItemRepository.saveAndFlush(f);

        // resolve
        mvc.perform(patch("/api/feedback/" + f.getId() + "/resolve").param("officerName","officer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RESOLVED")));

        // resolving again should be conflict
        mvc.perform(patch("/api/feedback/" + f.getId() + "/resolve").param("officerName","officer"))
                .andExpect(status().isConflict());

        // not found
        mvc.perform(patch("/api/feedback/00000000-0000-0000-0000-000000000000/resolve"))
                .andExpect(status().isNotFound());
    }

    @Test
    void retrievingAuditAndNotifications() throws Exception {
        Application app = createApp();
        // create audit & notification
        app.addAuditEntry(new AuditEntry(app, "system", "TEST", "detail"));
        app.addNotification(new Notification(app, "officer", "msg"));
        applicationRepository.saveAndFlush(app);

        mvc.perform(get("/api/applications/" + app.getId() + "/audit").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        mvc.perform(get("/api/applications/" + app.getId() + "/notifications").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }
}
