# Use Case Prioritisation: Delivering the Highest-Value Workflow Within Three Days

I made a deliberate decision to implement **Use Case 2** and defer Use Cases 1 and 3 based on 3 criteria in the order of highest business value, feasibility and complexity in this order.

My goal was to deliver one complete, production-oriented workflow rather than spread the available time across three partially implemented use cases.

This is also part of my Agile practice to deliver incremental artifacts at every sprint for the user to visualise the progress which greatly assist the sales team to and their sales presentations.

**Use Case 2**: Officer Application Review and Feedback** sits at the centre of the licensing process.

My primary motivation was to address a key operational bottleneck: simplify the complex interactions between officer and operator. By making the review and feedback workflow clearer, more structured and easier to manage, the system could streamline processes and increase the efficiency throughput which translates to dollar and cents in the real world.

The improved workflow also improves transaction traceability and the quality of feedback provided to operators. Each review decision, feedback item, status change and resubmission can be tracked consistently, creating a clearer audit trail and reducing ambiguity between both parties.

I have observed similar interaction patterns in the mission-critical government systems I have worked on. For example:

The eMobilisation system coordinated workflows between mobilisation administrators and mobilisation commanders.
The iBorders system supported operational interactions between ICA operators and ICA officers.

These experiences taught me that the efficiency of a system depends not only on its individual features, but also on how effectively it coordinates decisions, responsibilities and information across different user roles. This is why I prioritised Use Case 2: it addresses the operational handoffs that have the greatest influence on the efficiency, reliability and accountability of the overall licensing process.

To me, it is the use case with the highest business value.

## Why Use Case 1 and 3 are deferred

Use Case 1 was deferred because it seem simple but scope may be broader than it first appears. To guide the operator on entry is also a business value not as high as use case 2 because it involves 1 role. A typical production-quality operator submission workflow would require draft persistence, document uploads and asynchronous verification. A production-quality upload capability also introduces storage, file-size validation, malware scanning, MIME-type verification and access-control concerns. Delivering all of this properly in three days would be risky.

By deferring Use Case 1, the trade off is I did not build the complete application submission process for operators. I could have created a basic upload screen to make the system look more complete, but it would not have properly handled important requirements such as securely storing documents, validating uploaded files, saving drafts and preserving earlier submissions. I chose to focus on completing Use Case 2 properly instead of building a superficial version of Use Case 1.

Use Case 3 was deferred because it presented the greatest delivery uncertainty and contained several requirements that were not explicitly defined. For example, it was unclear whether officers would conduct on-site assessments using laptops, MacBooks, iPads or Android tablets such as Samsung devices.

Supporting multiple device types raises important technical and user-experience considerations, including responsive design, browser compatibility, network reliability, offline operation, autosaving, draft synchronisation, concurrent editing and conflict resolution.

The workflow also introduces a more complex data hierarchy involving site visits, checklists, individual checklist items, multiple clarification rounds, operator responses and supporting documents. These uncertainties would need to be clarified before selecting an appropriate architecture and implementing the workflow reliably.

By deferring Use Case 3, the trade off is I did not implement the on-site inspection workflow. Trying to build it within the same limited timeframe would have left less time to complete and properly test Use Case 2. Before implementing Use Case 3, I would also confirm whether officers need to continue working when there is no internet connection, as this requirement would significantly affect the system design.

## Here is what I will prioritise in this order

1. Use Case 2 — highest operational value and strongest fit within the available time
2. Use Case 1 — valuable next step for completing the operator-facing lifecycle
3. Use Case 3 — important, but dependent on further mobile and connectivity requirements

There were several broader trade-offs behind my decision:

| Decision                              | Benefit                                                    | Trade-off                                                                        |
| ------------------------------------- | ---------------------------------------------------------- | -------------------------------------------------------------------------------- |
| One complete vertical slice           | Demonstrates an end-to-end working outcome                 | Less overall feature coverage                                                    |
| Use Case 2 first                      | Addresses the platform’s operational centre                | Operator submission and site assessment remain incomplete                        |


One important limitation is that frontend-only validation and error translation improve the user experience but are not a complete enforcement mechanism. In a production system, the backend must remain the source of truth, return structured domain errors, and enforce the same rules regardless of which client calls the API.

In summary, I did not defer Use Cases 1 and 3 because of a value proposition. I deferred them because completing all three would have produced excessive breadth and insufficient depth. The decision reflects how I would approach a real mission-critical system: prioritise the highest-value workflow, protect data integrity, deliver it end to end, and document the remaining capabilities as deliberate next steps.

## Design patterns used in the project

This project uses a small set of practical design patterns that are appropriate for a mission-critical workflow system:

### 1. Layered architecture
The backend is structured in layers: web controllers, service layer, domain model and repository layer.

Why it is used:
- keeps responsibilities separate
- makes it easier to reason about HTTP concerns, business rules and persistence independently
- reduces coupling between UI/API logic and data logic
- allows the workflow to evolve without rewriting core business rules

This is visible in the split between `ApplicationsController`, `OfficerReviewService`, domain entities and repository interfaces.

### 2. Domain-driven design (DDD-lite)
The core business logic sits in the domain model rather than being spread across controllers or UI code.

Why it is used:
- status transitions and workflow rules are explicitly expressed in the domain
- it protects business invariants and prevents invalid application states
- it makes the workflow easier to audit and validate

Examples in the codebase include `Application`, `ApplicationStatus`, and `DomainRules`.

### 3. Repository pattern
Persistence logic is abstracted behind repository interfaces.

Why it is used:
- separates the domain from database concerns
- makes the service layer cleaner and easier to test
- allows the app to change persistence implementation without changing workflow logic

This pattern is used through repositories such as `ApplicationRepository` and `FeedbackItemRepository`.

### 4. DTO pattern
The backend exposes purpose-specific DTOs instead of exposing entities directly.

Why it is used:
- keeps the API contract stable and explicit
- prevents internal domain objects from being overexposed
- supports current UI needs without leaking persistence concerns

Examples include `ApplicationReviewDTO`, `RevisionDTO`, and `ApplicationListItemDTO`.

### 5. Service layer / orchestration pattern
Workflow behaviour is coordinated in service classes rather than in controllers.

Why it is used:
- controllers stay focused on HTTP concerns
- business workflow orchestration is centralized
- actions such as feedback creation, status changes, and notifications are coordinated consistently

`OfficerReviewService` is the main example of this pattern.

### 6. State machine pattern
Application status transitions are represented as an explicit state model.

Why it is used:
- the licensing workflow is rules-driven and status-dependent
- valid progression must be enforced, not guessed by the UI
- it improves traceability and prevents invalid workflow transitions

This is implemented via `ApplicationStatus` and `DomainRules.isAllowedTransition(...)`.

### 7. Event-style notification pattern
The system adds audit entries and notifications when important workflow events happen.

Why it is used:
- supports traceability and accountability
- makes operational visibility stronger for officers and operators
- supports a robust compliance and review trail

Examples include notifications sent when status changes or operator resubmission occurs.

### 8. Frontend composition and route-based view pattern
The React app is split into route-driven page components and reusable UI elements.

Why it is used:
- allows officer and operator workflows to remain separate while reusing shared data and logic
- makes the UI easier to maintain and extend
- clarifies role-specific responsibilities without duplicating all behaviour

This is visible in the `App.tsx` route selection and the `OfficerReview` / `OperatorReview` page components.

### 9. Dependency injection
Spring Boot uses dependency injection for services, repositories and other collaborators.

Why it is used:
- reduces tight coupling
- improves testability
- supports cleaner component wiring in a production-grade application

This pattern is central to the backend architecture.

Overall, the project uses a pragmatic combination of layered architecture, domain-driven rules, repository abstraction and workflow-driven state management. These patterns are appropriate because the system is not just a CRUD demo; it models a real operational workflow with status transitions, auditability, notifications and role-based decisions.