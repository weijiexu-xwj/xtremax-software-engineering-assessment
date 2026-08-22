## Prompt 0:

Act as an experienced senior software engineer with 16 years of mission critical engineering. Analyse Use Case 2 using the CARE framework:

* Context: Understand the problem, users and desired outcome.
* Actions: Identify the tasks and features to implement.
* Requirements and Constraints: Extract the business rules, technical requirements, limitations and assumptions.
* Expected Output: Define the required deliverables and acceptance criteria.

Then create a sequence of simple, copy-and-paste-ready prompts that can be run individually in agent mode to generate the application one layer at a time:

1. Project setup and domain model
2. DAO/data-access layer
3. Transaction/service layer
4. Application and REST API layer
5. UI layer
6. Integration and testing

Each prompt must:

* Build on the output of the previous prompt.
* Tell the agent to inspect the existing repository first.
* Clearly define what to create or modify.
* Generate only the specified layer.
* Avoid changing unrelated code.
* Include relevant tests and validation.
* Run the tests and report the files changed.
* Produce simple, complete and working code.

Keep the architecture and setup as simple as possible. Avoid unnecessary frameworks, abstractions, design patterns and infrastructure.

Do not generate the application code. Generate only the CARE analysis, key assumptions and the ordered agent-mode prompts.

## Prompt 1:
Act as a senior software engineer with 16 years of mission critical engineering working in agent mode.

Inspect the existing repository before making any changes. Identify the existing files, build tools, frameworks and coding conventions. Preserve all existing work and do not change unrelated code.

This is the first implementation step for Use Case 2: Officer Application Review and Feedback.

Create only the project foundation and domain model.

If the repository does not already have a backend, create a simple Gradle-based Java 21 Spring Boot application under `backend`.

Use only the dependencies needed for:

* Spring Web
* Spring Data JPA
* Bean Validation
* H2
* Spring Boot Test

Do not add security, Flyway, Docker, Lombok, MapStruct, Testcontainers, messaging or cloud dependencies.

Create a simple domain model for:

* Application
* ApplicationStatus
* ApplicationRevision
* ApplicationField
* ApplicationDocument
* AIVerificationResult
* FeedbackItem
* FeedbackTargetType
* FeedbackStatus
* CommentTemplate
* AuditEntry
* Notification

Model the relationships required to support multiple revisions, contextual feedback, audit history and notifications.

Requirements:

* Use UUID identifiers or one consistent simple identifier strategy.
* An Application has a unique reference number.
* An Application has a current internal status.
* Add an optimistic-lock version field to Application.
* An Application has multiple revisions.
* Revision numbers must be sequential within an application.
* Earlier revisions must not be modified when a new revision is created.
* Feedback must identify whether it targets a form field or document.
* Feedback must store the target key, comment, status and timestamps.
* Define all internal statuses and Officer/Operator labels provided by Use Case 2.
* Define which MVP status transitions are allowed.
* Do not implement repository, service, controller or UI code yet.
* Avoid unnecessary base classes, interfaces and design patterns.

Add focused unit tests for:

* Officer and Operator status labels
* Allowed status transitions
* Rejected status transitions
* Feedback status
* Basic domain validation

Run the backend tests.

When finished, report:

1. Repository structure found
2. Assumptions made
3. Files created or modified
4. Domain model implemented
5. Tests added
6. Commands run and actual results
7. Anything deferred

Generate simple, complete and compiling code for this layer only.

## Prompt 2:
Act as a senior software engineer with 16 years of mission critical engineering working in agent mode.

Inspect the existing repository and review the project setup and domain model created in the previous step. Preserve the existing architecture and do not change unrelated code.

Implement only the DAO and data-access layer for Use Case 2.

Use Spring Data JPA and H2. Keep the implementation simple. Do not introduce repository adapters, separate persistence models, QueryDSL or additional database frameworks.

Create repositories for:

* Application
* ApplicationRevision
* FeedbackItem
* CommentTemplate
* AuditEntry
* Notification

Add only the repository queries required to:

* Find an application by ID
* Find an application by reference number
* Retrieve application revisions ordered by revision number
* Retrieve a particular application revision
* Retrieve open feedback for an application
* Retrieve feedback for a particular revision
* Retrieve comment templates
* Retrieve audit entries in chronological order
* Retrieve notifications for an application

Configure H2 for local development and testing.

Add appropriate JPA constraints and indexes for:

* Unique application reference number
* Unique revision number within an application
* Application status
* Open feedback lookup
* Application audit-history lookup
* Application notification lookup

Create deterministic DAO test data where needed.

Add `@DataJpaTest` tests for:

* Saving and retrieving an application
* Finding an application by reference number
* Saving multiple revisions
* Enforcing unique revision numbers
* Retrieving revisions in the correct order
* Retrieving open feedback
* Retrieving comment templates
* Retrieving audit entries in the correct order
* Optimistic-lock behaviour

Do not implement service, transaction, controller, REST API or UI code.

Run all backend tests.

When finished, report:

1. Existing structure inspected
2. Assumptions made
3. Files created or modified
4. Repositories and queries implemented
5. Database constraints and indexes added
6. Tests added
7. Commands run and actual results
8. Known limitations

Generate simple, complete and working code for the DAO layer only.

## Prompt 3:
Act as a senior software engineer with 16 years of mission critical engineering working in agent mode.

Inspect the existing repository and review the domain and DAO layers created in the previous steps. Preserve their conventions and do not change unrelated code.

Implement only the transaction and service layer for Use Case 2.

Use Spring services with constructor injection and `@Transactional`. Do not create a separate transaction framework or unnecessary service interfaces.

Implement services for these operations:

1. Retrieve an application for Officer review.
2. Add contextual feedback to a form field or document.
3. List predefined comment templates.
4. Request pre-site resubmission.
5. Record an Operator resubmission as a new revision.
6. Compare two application revisions.
7. Resolve an open feedback item.
8. Retrieve the application audit history.
9. Retrieve application notifications.

Business rules:

* Feedback must target an existing field or document in the selected revision.
* Feedback comments must not be blank.
* Requesting resubmission requires at least one open feedback item.
* Requesting resubmission changes the status to `PENDING_PRE_SITE_RESUBMISSION`.
* The status change, audit entry and Operator notification must be saved in one transaction.
* Recording a resubmission creates a new revision instead of modifying the previous revision.
* Recording a resubmission changes the status to `PRE_SITE_RESUBMITTED`.
* Recording a resubmission creates an audit entry and Officer notification.
* Revision numbers must increase sequentially.
* Invalid status transitions must be rejected.
* Resolving feedback records the resolving Officer and timestamp.
* Feedback that is already resolved cannot be resolved again.
* Every important state-changing action must create an audit entry.
* Optimistic-lock conflicts must not be silently ignored.

Revision comparison must:

* Match form data using stable field keys.
* Match documents using stable document IDs.
* Identify added, removed and modified items.
* Compare document metadata only.
* Return a simple comparison result suitable for the REST API and UI.

Add service tests for:

* Successful feedback creation
* Invalid feedback target
* Requesting resubmission without feedback
* Successful request for resubmission
* Invalid status transition
* Status, audit and notification creation
* Transaction rollback when one part fails
* Creation of a new immutable revision
* Multiple resubmission rounds
* Revision comparison
* Successful feedback resolution
* Repeated feedback resolution
* Optimistic-lock conflict where practical

Do not implement controllers, API DTOs or UI code.

Run all backend tests.

When finished, report:

1. Existing layers inspected
2. Assumptions made
3. Files created or modified
4. Services and transactions implemented
5. Business rules enforced
6. Tests added
7. Commands run and actual results
8. Known limitations

Generate simple, complete and working code for this layer only.

## Prompt 4:
Act as a senior software engineer with 16 years of experience building mission-critical systems working in agent mode.

Inspect the existing repository and review the domain, DAO and service layers created in the previous steps. Preserve their conventions and do not change unrelated code.

Implement only the application and REST API layer for Use Case 2.

Create simple request and response DTOs, manual mapper methods, REST controllers and a global exception handler.

Do not expose JPA entities directly.

Implement these endpoints, adjusting paths only if the repository already has a clear convention:

* `GET /api/applications/{applicationId}/review`
* `GET /api/applications/{applicationId}/revisions`
* `GET /api/applications/{applicationId}/revisions/compare?from={fromRevision}&to={toRevision}`
* `POST /api/applications/{applicationId}/feedback`
* `POST /api/applications/{applicationId}/request-information`
* `PATCH /api/feedback/{feedbackId}/resolve`
* `GET /api/comment-templates`
* `GET /api/applications/{applicationId}/audit`
* `GET /api/applications/{applicationId}/notifications`

API requirements:

* Return all form data, document metadata and AI-verification findings required by the Officer review page.
* Return contextual feedback with its target.
* Return the Officer-facing status label.
* Return revision-comparison results containing added, removed and modified items.
* Validate required fields and comment length.
* Return clear errors for invalid input.
* Return `404` when an application or feedback item is not found.
* Return `409` for invalid status transitions and optimistic-lock conflicts.
* Use one consistent JSON error structure.
* Add simple pagination only where it is already needed; do not build a generic pagination framework.
* Add simple CORS configuration for the local frontend.
* Do not add authentication unless it already exists in the repository.
* Do not add OpenAPI tooling unless it already exists.

Add controller tests using MockMvc for:

* Retrieving an application review
* Adding feedback
* Invalid feedback input
* Requesting more information
* Invalid status transition
* Comparing revisions
* Resolving feedback
* Application not found
* Conflict response
* Retrieving audit history
* Retrieving notifications

Do not create or modify frontend code.

Run all backend tests.

When finished, report:

1. Existing layers inspected
2. Assumptions made
3. Files created or modified
4. Endpoints implemented
5. Validation and error handling added
6. Tests added
7. Commands run and actual results
8. Example request and response shapes
9. Known limitations

Generate simple, complete and working code for this layer only.

## Prompt 5:

Act as a senior frontend engineer with 16 years of experience specialising in developing React for high performance interactive mission critical applications that requires low latency in agent mode.

Inspect the entire existing repository and review service layer created, the REST API created and its endpoints exposed in the previous steps. Preserve existing conventions and do not change unrelated backend code.

Implement only the React UI layer for the Officer Application Review workflow according to the specifications in use-case-2-officer-review.md

If no frontend exists, create a simple React application under `frontend` using:

* Vite
* TypeScript strict mode
* React
* Native `fetch`
* Plain CSS or the project’s existing styling approach
* Vitest
* React Testing Library

Do not add Redux, a large component library, Tailwind, Axios or unnecessary state-management frameworks.

Keep the design simple

Create:

* Typed API models
* A small reusable API client
* An Officer application-review page
* Form-section display
* Document and AI-verification display
* Feedback form
* Comment-template selector
* Open and resolved feedback display
* Request-information action
* Revision selector
* Revision-comparison display
* Audit-history display
* Notification display
* Loading, empty, success and error states

The Officer page must allow the user to:

1. View the application reference, status and current revision.
2. Review form data in organised sections.
3. Review documents and AI-verification findings.
4. Add feedback to a specific field or document.
5. Select and edit a predefined comment template.
6. See open and resolved feedback.
7. Request additional information.
8. Select and compare two revisions.
9. See added, removed and modified information.
10. Review the audit history and notifications.

UI requirements:

* Use semantic HTML.
* Label all form controls.
* Support keyboard navigation.
* Provide visible focus states.
* Do not rely on colour alone to communicate status.
* Keep components small but avoid unnecessary fragmentation.
* Keep REST calls in the API client rather than individual presentational components.
* Do not duplicate authoritative backend status-transition logic.
* Show a useful message for validation, not-found, conflict and server errors.
* For a conflict, tell the Officer that the application changed and should be refreshed.
* Configure the API base URL through a Vite environment variable.

Add frontend tests for:

* Loading the application
* Displaying form data and documents
* Displaying AI-verification issues
* Creating contextual feedback
* Selecting a comment template
* Requesting additional information
* Displaying a conflict error
* Comparing revisions
* Resolving feedback
* Displaying audit entries

Run:

* Frontend tests
* Type checking
* Linting if configured
* Production build

When finished, report:

1. Existing repository inspected
2. Assumptions made
3. Files created or modified
4. UI features implemented
5. Tests added
6. Commands run and actual results
7. Known limitations

Generate simple, complete and working frontend code only. Do not redesign or refactor the backend.

## Prompt 6
Act as an experienced H2 database administrator working in agent mode. Inspect the existing repository and database schema, then prepare and populate realistic mock data for local development and frontend integration testing.

Context

The following application tables currently contain no data:

* `AIVERIFICATION_RESULT`
* `APPLICATION`
* `APPLICATION_DOCUMENT`
* `APPLICATION_FIELD`
* `APPLICATION_REVISION`
* `AUDIT_ENTRY`
* `COMMENT_TEMPLATE`
* `FEEDBACK_ITEM`
* `NOTIFICATION`
* `USERS`

`INFORMATION_SCHEMA` and `PG_CATALOG` are system-managed schemas. Inspect them only if necessary; do not insert, update or delete their contents.

Actions

1. Inspect the existing repository before making changes, including:

    * JPA entities and table mappings.
    * Column definitions and data types.
    * Primary keys, foreign keys and unique constraints.
    * Nullable and mandatory fields.
    * Enum values and validation rules.
    * Existing Flyway migrations, schema scripts, data scripts or application initialisers.

2. Determine the correct dependency order for inserting records.

3. Create five realistic mock records for each table listed above.

4. Ensure the records form a coherent dataset. For example:

    * Applications belong to valid users.
    * Revisions belong to existing applications.
    * Documents and fields reference valid applications or revisions.
    * AI verification results reference the appropriate application data.
    * Feedback, audit entries and notifications reference valid applications and users.
    * Comment templates contain realistic officer-review comments.

5. Use stable UUIDs so the frontend can reliably load a known application, including through endpoints such as:

```text
GET /api/applications/{applicationId}/review
GET /api/applications/{applicationId}/revisions
```

6. Use JSON data.sql because I prefer SQL-based seeding.

7. Make the seed process safe and repeatable. Do not delete or overwrite existing user data. If the tables are no longer empty, avoid creating duplicate records.

8. Start the application and verify that:

    * Database initialisation succeeds without constraint violations.
    * Each target application table contains the expected records.
    * All foreign-key relationships are valid.
    * At least one seeded application can be retrieved through the existing REST API.

Requirements and Constraints

* Modify only files required for development data initialisation.
* Preserve the existing schema and application behaviour.
* Do not modify H2 system schemas or tables.
* Do not disable referential integrity to bypass invalid relationships.
* Do not invent columns, enum values or relationships that are not present in the schema.
* Do not add unnecessary dependencies or frameworks.
* Use realistic but entirely fictional names and data.
* Do not include genuine personal, confidential or production information.
* If creating five records in every child table would violate the domain model, explain the conflict and create the closest valid coherent dataset instead.
* Confirm the exact table name `AIVERIFICATION_RESULT` from the entity mapping before using it.
* Handle `USERS` carefully if it is a reserved identifier or requires quoting in H2.

Expected Outcome

When finished, report:

1. The files created or modified and why.
2. The insertion order used.
3. The number of records created in each table.
4. The stable UUIDs of the five mock applications.
5. One example URL that can be used to load a seeded application from the React frontend.
6. The validation commands and SQL queries executed, with their results.
7. Any assumptions, schema inconsistencies or risks discovered.

## Prompt 7:

Act as a senior full-stack software engineer working in agent mode.

Inspect the complete repository and review all layers produced by the previous prompts. Do not add new product features and do not refactor unrelated code.

Integrate and verify the complete Use Case 2 vertical slice.

Create deterministic development data for one representative application containing:

* A reference number
* Multiple form sections
* At least two documents
* One successful AI-verification result
* One flagged AI-verification issue
* Several comment templates
* At least two revisions with known differences
* Open feedback
* Resolved feedback
* Audit entries
* Notification records

Ensure development data is created only for the local or development profile.

Verify this workflow:

1. Start the backend.
2. Start the frontend.
3. Open the seeded application.
4. View form data, documents and AI findings.
5. Add contextual feedback.
6. Request additional information.
7. Verify the status, audit entry and notification.
8. Compare two revisions.
9. Resolve an open feedback item.
10. Verify the updated feedback and audit history.

Add or complete integration tests for:

* Application review retrieval
* Contextual feedback creation
* Request-information transaction
* Status transition
* Audit and notification creation
* Revision comparison
* Feedback resolution
* Multiple revision rounds
* Failed transaction rollback
* API validation and error responses

Do not add Playwright, Cypress, Docker or additional infrastructure unless it already exists in the repository. Prefer Spring Boot integration tests and React component tests.

Run:

* Backend clean build
* All backend tests
* Frontend type checking
* All frontend tests
* Frontend production build
* A local API smoke test if the environment permits it

Fix only failures related to the implemented scope.

When finished, report:

1. Repository and layers inspected
2. Integration problems found
3. Corrections made
4. Files created or modified
5. Complete verification commands
6. Actual test and build results
7. Acceptance criteria satisfied
8. Remaining limitations

Produce a simple, complete and working integrated application. Do not expand the scope.


## Here are examples of prompts that does validation iteratively at each layer

### Example 1 (backend):
Act as a senior software engineer with 16 years of experience building mission-critical systems.

Inspect the existing repository, including the project setup, domain model, and DAO/data-access layer. Review the implementation against the requirements in `use-case-2-officer-review.md`.

Provide a focused code and design review covering:

* Whether the implementation correctly and completely satisfies the use case
* Missing, misunderstood, or incorrectly implemented requirements
* Domain-model and data-access design issues
* Data integrity, validation, transaction, concurrency, security, and error-handling concerns
* Unnecessary complexity, duplication, or premature abstractions
* Test coverage gaps and important edge cases
* Specific improvements, with clear reasoning and practical recommendations

Prioritise your findings by severity: Critical, High, Medium, and Low. For each finding, reference the relevant requirement and file or code location where possible.

Do not modify any files or generate code. Conclude with:

1. An overall assessment of the implementation
2. The highest-priority changes required before proceeding to the next layer
3. What is already well designed and should be retained
4. Any assumptions or unclear requirements that need confirmation


### Example 2 (frontend):
Act as a senior React frontend developer experienced in building mission-critical applications. Work in agent mode and implement navigation between applications

Context

The React frontend currently displays only one application. The Officer view and Operator view must allow users to review and navigate between multiple applications.

Actions

1. Inspect the existing repository, frontend architecture, components, routing, state management, and `use-case-2-officer-review.md`.
2. Allow users to navigate between available applications.
3. Display the selected application’s details, status, form data, documents, notifications, feedback, and revisions as required.
4. Reuse existing components and logic where appropriate.

Requirements and Constraints

* Preserve the existing workflows and functionality.
* Ensure application navigation updates all displayed information correctly.
* Keep the implementation simple, responsive, and maintainable.
* Avoid duplicating logic or introducing unnecessary dependencies.
* Do not modify unrelated backend functionality.
* Add or update relevant tests.

Expected Output

* Navigation between multiple applications.
* Passing frontend tests and production build.
* A concise summary of:

    * Files created or modified
    * Navigation between applications implemented
    * Tests and validation performed
    * Assumptions or specification gaps identified