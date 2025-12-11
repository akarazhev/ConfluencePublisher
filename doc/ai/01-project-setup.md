# Prompt 01: Project Setup and Configuration

## Role

You are an expert Java engineer and expert front-end engineer.

## Task

Create the initial project structure and configuration for a full-stack web application called **"Confluence Publisher"
** - a tool for creating, scheduling, and publishing pages to Atlassian Confluence.

## Technology Stack

- **Backend**: Spring Boot 3.2, Java 21, Gradle (Kotlin DSL)
- **Frontend**: Angular 20, TypeScript, TailwindCSS
- **Database**: SQLite with Hibernate/JPA (using `hibernate-community-dialects`)
- **Containerization**: Docker or Podman with multi-stage builds

## Build and Runtime Requirements

- The backend and frontend MUST be buildable and runnable using only Docker or Podman together with a Compose-compatible
  CLI (e.g. `docker compose` or `podman compose`).
- Local installation of Java, Gradle, Node.js, or Angular CLI MUST NOT be required for building and running the
  application; these tools may be used optionally for local development, but containerized workflows are the primary
  supported path.

## Project Structure Requirements

Create a monorepo with:

- `backend/` - Spring Boot application
- `frontend/` - Angular application
- `data/` - SQLite database directory (gitignored)
- `storage/attachments/` - File upload directory (gitignored)
- Root-level Docker Compose and environment files

## Backend Requirements

1. **Gradle build** with Spring Boot starters: web, data-jpa, validation, actuator
2. **SQLite database** using `org.xerial:sqlite-jdbc` and `hibernate-community-dialects`
3. **Lombok** for boilerplate reduction
4. **application.yml** with configurable properties:
    - Database URL (default: `jdbc:sqlite:./data/app.db`)
    - Attachment directory (default: `storage/attachments`)
    - Confluence URL, username, API token, default space (all from environment variables)
    - CORS origins list (localhost:4200, 8080, 5173)
    - Provider type (confluence-server or confluence-stub)
    - Scheduler interval in seconds
5. **Multipart upload** support up to 50MB
6. **Actuator** health endpoint exposed

## Frontend Requirements

1. **Angular 20** with standalone components (no NgModules)
2. **TailwindCSS** for styling
3. **Environment files** with configurable API base URL
4. **TypeScript strict mode** enabled

## Configuration Files Needed

- `backend/build.gradle.kts` - Gradle build with all dependencies
- `backend/settings.gradle.kts` - Project name
- `backend/src/main/resources/application.yml` - Spring configuration
- `backend/src/main/java/com/confluence/publisher/ConfluencePublisherApplication.java` - Main application class (
  placeholder)
- `frontend/package.json` - npm dependencies
- `frontend/angular.json` - Angular CLI configuration
- `frontend/tailwind.config.js` - TailwindCSS setup
- `frontend/tsconfig.json` - TypeScript configuration
- `frontend/src/main.ts` - Angular bootstrap entry point
- `frontend/src/index.html` - HTML template
- `frontend/src/styles.css` - Global styles with Tailwind imports
- `frontend/src/environments/environment.ts` - Development environment
- `frontend/src/environments/environment.prod.ts` - Production environment
- `.env.example` - Environment variable template
- `.gitignore` - Ignore data/, node_modules/, build artifacts

## Verification Criteria

- Backend compiles and starts with `./gradlew bootRun` on port 8080
- Frontend installs and starts with `npm install && npm start` on port 4200
- No compilation errors in either project
- Ready for adding business logic in subsequent prompts
