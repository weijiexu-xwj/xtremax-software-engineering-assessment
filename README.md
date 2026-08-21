# Xtremax Software Engineering Assessment

## 1) What the application is about

This project implements a licensing workflow system with role-based review flows:

- **Operator flow**: submit/resubmit application data and documents.
- **Officer flow**: review submissions, add contextual feedback, request resubmission, compare revisions, and track audit/notifications.

The solution is split into:

- **Backend**: Spring Boot REST API with domain workflow/status transitions.
- **Frontend**: React UI for Officer and Operator views, including application navigation and status mapping.

The backend seeds sample data (applications, revisions, feedback, audit entries, notifications) so the UI is immediately usable in local development.

---

## 2) How to run backend + frontend (and in which order)

### Prerequisites

- **Java 21**
- **Node.js 18+** (recommended: current LTS)
- **npm 9+**

### Run order

Run in this order:

1. **Start backend first** (frontend depends on backend API).
2. **Start frontend second**.

### Step A — Start backend (Spring Boot)

From repo root:

#### Windows (PowerShell / CMD)
```bash
cd backend
.\gradlew.bat bootRun
```

#### macOS / Linux
```bash
cd backend
./gradlew bootRun
```

Backend runs at:

- `http://localhost:8080`
- API base: `http://localhost:8080/api`
- H2 console: `http://localhost:8080/h2-console`

H2 connection settings:

- JDBC URL: `jdbc:h2:mem:assessment;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- Username: `sa`
- Password: *(empty)*

### Step B — Start frontend (React + Vite)

Open a second terminal, from repo root:

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at:

- `http://localhost:5173`

Environment variable (already present in `frontend/.env.development`):

- `VITE_API_BASE_URL=http://localhost:8080/api`

### Useful verification commands

#### Backend tests
```bash
cd backend
./gradlew test
```
Windows:
```bash
cd backend
.\gradlew.bat test
```

#### Frontend checks
```bash
cd frontend
npm run typecheck
npm run test
npm run build
```

---

## 3) Tech stack summary

### Backend

- **Java 21**
- **Spring Boot 3.2.x**
  - Spring Web (REST API)
  - Spring Data JPA
  - Spring Validation
- **H2 in-memory database**
- **Gradle** build system
- **JUnit 5 / Spring Boot Test**

### Frontend

- **React 18**
- **TypeScript**
- **Vite 5**
- **Vitest + Testing Library**

### Architecture

- RESTful API backend with seeded local data.
- Single-page frontend consuming backend API endpoints.
- Role-based UI behavior (Officer / Operator) with shared application model and status mapping.
