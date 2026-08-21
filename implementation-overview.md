# Implementation Overview

![Implementation Overview](implementation-overview.svg)

This repository infographic summarises the final **67 implementation files** supporting Use Case 2:

- **48 backend files** covering configuration, domain rules, persistence, transactional services, REST contracts and automated tests.
- **19 frontend files** covering React/Vite configuration, typed API access, officer and operator views, application navigation and automated tests.

## Principal workflow

`OfficerReview.tsx` → `ApplicationsController.java` → `OfficerReviewService.java` → `ApplicationRepository.java`

The workflow keeps UI concerns, HTTP contracts, business transactions and persistence responsibilities separate while preserving traceability across status transitions and submission revisions.

## Principal files

| File | Responsibility |
|---|---|
| `Application.java` | Owns status transitions, revision sequencing, optimistic locking, audit entries and notification creation. |
| `ApplicationRevision.java` | Preserves each submitted application version as an immutable, numbered revision. |
| `OfficerReviewService.java` | Coordinates feedback, resubmission, revision, audit and notification changes within transactions. |
| `ApplicationsController.java` | Exposes review, feedback, comparison, resubmission, audit, notification and application-list endpoints. |
| `ApiMapper.java` and DTOs | Convert persistence entities into stable, typed and role-appropriate API contracts. |
| `OfficerReview.tsx` | Implements the officer’s review, feedback, comparison and resubmission experience. |
| `OperatorReview.tsx` and `App.tsx` | Present operator-facing statuses and preserve application context between role views. |

## File-family summary

| File family | Count | Purpose |
|---|---:|---|
| Backend configuration | 4 | Gradle build and H2 development/test profiles. |
| Domain model | 13 | Licensing state, revisions, feedback, verification, audit and notifications. |
| Persistence | 6 | Spring Data JPA repositories and ordered application-history retrieval. |
| Transaction service | 1 | Atomic officer-review workflow orchestration. |
| REST API | 20 | Controllers, DTOs, mapping, CORS and consistent error handling. |
| Backend tests | 4 | Domain, DAO, service and MockMvc regression coverage. |
| Frontend configuration | 6 | React/Vite/TypeScript setup, environment and documentation. |
| Frontend core | 6 | App bootstrap, routing, styling, API client, types and status mapping. |
| Views and components | 4 | Officer review, operator review, document findings and application selection. |
| Frontend tests | 3 | Officer/operator rendering, interaction, routing and error-message coverage. |