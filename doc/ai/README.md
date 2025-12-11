# Confluence Publisher - AI Generation Prompts

## Prompt Usage Guide

- **Recommended models**: Use a modern, code-capable LLM with strong Java, Spring Boot, TypeScript, and Angular support
  (for example, GPT-4-level or later models, or an equivalent code-focused model).
- **Temperature**: 0.0–0.3 for repeatable, deterministic code generation. Prefer lower values (0.0–0.1) when you want
  the model to strictly follow these prompts and avoid creative deviations.
- **How to feed prompts**:
    - Provide **one prompt file at a time**, in numerical order (01 → 13).
    - Paste the **entire markdown content** of the prompt into the chat.
    - Instruct the model to **modify or create files in-place** in the existing repository to satisfy the prompt,
      without changing behavior defined by earlier prompts unless strictly necessary.
- **Framing suggestion** (you can adapt this when sending each prompt):
    - “You are acting as my pair programmer. Using the current codebase plus the following prompt, update or add code so
      that all requirements in this prompt are met. Keep the implementation simple and explicit. Do not introduce new
      features beyond what this prompt requires.”
- **Workflow**:
    - After each prompt, run builds/tests (Gradle for backend, npm/Angular for frontend, or Docker/Compose) and fix any
      issues **before** moving to the next prompt.

### Example Conversation (Prompt 01)

```text
User: You are acting as my pair programmer. Using the existing codebase in this repository and the prompt below, update
the project so that all requirements are met. Keep the implementation simple and explicit. Do not introduce new
features beyond what this prompt requires.

Here is Prompt 01 (Project Setup and Configuration):

[paste full contents of 01-project-setup.md here]

Assistant: Understood. I will:
1) Create or update the backend Gradle project, application.yml, and main class.
2) Create or update the Angular frontend project with Tailwind and strict TypeScript config.
3) Ensure the project structure, defaults, and Docker/Compose expectations are satisfied.
Then I’ll summarize the changes and any follow-up steps.
```

This directory contains **13 detailed prompts** for AI to generate a complete full-stack web application called "
Confluence Publisher". Each prompt describes requirements without providing source code, allowing AI to generate the
implementation.

## Technology Stack

- **Backend**: Spring Boot 3.2, Java 21, JPA/Hibernate, SQLite
- **Frontend**: Angular 20, TypeScript, TailwindCSS
- **Deployment**: Docker or Podman, Compose (e.g. `docker compose` / `podman compose`), Nginx

## Build and Runtime Requirements

- The full application (backend and frontend) MUST be buildable and runnable on a host where only Docker or Podman and
  a Compose-compatible CLI (e.g. `docker compose` or `podman compose`) are installed.
- Local installations of Java, Gradle, Node.js, or Angular CLI MUST NOT be required to build or run the system; they may
  be used optionally for local development, but containerized workflows are the primary supported path.

## Prompt Sequence

Execute these prompts in order:

| #  | File                                                                                   | Description                             |
|----|----------------------------------------------------------------------------------------|-----------------------------------------|
| 01 | [01-project-setup.md](01-project-setup.md)                                             | Project structure, Gradle/npm config    |
| 02 | [02-backend-entities-repositories.md](02-backend-entities-repositories.md)             | 5 JPA entities, 5 repositories          |
| 03 | [03-backend-dtos.md](03-backend-dtos.md)                                               | Request/Response DTOs                   |
| 04 | [04-backend-configuration.md](04-backend-configuration.md)                             | Spring config classes                   |
| 05 | [05-backend-services.md](05-backend-services.md)                                       | Business logic services                 |
| 06 | [06-backend-providers.md](06-backend-providers.md)                                     | Confluence API providers                |
| 07 | [07-backend-controllers.md](07-backend-controllers.md)                                 | REST controllers                        |
| 08 | [08-backend-scheduler-exception-handler.md](08-backend-scheduler-exception-handler.md) | Scheduler, error handling               |
| 09 | [09-frontend-setup-routing.md](09-frontend-setup-routing.md)                           | Angular setup, routing                  |
| 10 | [10-frontend-api-service.md](10-frontend-api-service.md)                               | HTTP API service                        |
| 11 | [11-frontend-compose-component.md](11-frontend-compose-component.md)                   | Compose page                            |
| 12 | [12-frontend-schedules-component.md](12-frontend-schedules-component.md)               | Schedules page                          |
| 13 | [13-docker-deployment.md](13-docker-deployment.md)                                     | Container (Docker/Podman) configuration |

## How to Use

1. Start with prompt 01 and provide it to an AI assistant
2. Review and test the generated code
3. Proceed to the next prompt
4. Each prompt builds on the previous ones

## Application Features

- **Page Creation**: Create pages with title, content, attachments
- **File Attachments**: Upload and attach files to pages
- **Immediate Publishing**: Publish pages directly to Confluence
- **Scheduled Publishing**: Queue pages for background publication
- **Content Suggestions**: AI-powered content improvement (stub)
- **Provider Pattern**: Switchable Confluence providers (stub/server)

## API Endpoints

| Method | Endpoint                     | Description                     |
|--------|------------------------------|---------------------------------|
| GET    | /api/health                  | Health check                    |
| GET    | /api/config                  | Frontend configuration          |
| POST   | /api/pages                   | Create page                     |
| GET    | /api/pages/{id}              | Get page                        |
| POST   | /api/attachments             | Upload file                     |
| POST   | /api/schedules               | Create schedule                 |
| GET    | /api/schedules               | List schedules                  |
| GET    | /api/schedules/{id}          | Get schedule                    |
| POST   | /api/confluence/publish      | Publish immediately             |
| POST   | /api/ai/improve-content      | Content suggestions             |
| POST   | /api/ai/generate-description | Generate attachment description |

## Database Tables

- **page**: id, title, content, spaceKey, parentPageId, authorId, timestamps
- **attachment**: id, filename, contentType, size, storagePath, description
- **pageattachment**: id, pageId, attachmentId, position
- **schedule**: id, pageId, scheduledAt, status, attemptCount, lastError
- **publishlog**: id, pageId, provider, spaceKey, confluencePageId, status, message, createdAt

## Quick Start

### Development (optional local toolchain)

```bash
# Backend
cd backend && ./gradlew bootRun

# Frontend
cd frontend && npm install && npm start
```

### Docker / Podman (containerized, recommended)

```bash
cp .env.example .env
# Docker
docker compose up --build

# or Podman
podman compose up --build
```
