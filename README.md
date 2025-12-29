# Confluence Publisher

A full-stack web application for creating, scheduling, and publishing pages to Atlassian Confluence.

## Technology Stack

- **Backend**: Spring Boot 3.2, Java 21, Gradle (Kotlin DSL)
- **Frontend**: Angular 20, TypeScript, TailwindCSS
- **Database**: SQLite with Hibernate/JPA
- **Containerization**: Docker/Podman

## Prerequisites

- Docker or Podman with Compose support
- (Optional for local development) Java 21, Node.js 22+

## Quick Start with Docker/Podman

1. Clone the repository:
```bash
git clone <repository-url>
cd ConfluencePublisher
```

2. Copy the environment template:
```bash
cp .env.example .env
```

3. Edit `.env` file with your Confluence credentials (optional for initial setup)

4. Build and run with Docker Compose:
```bash
docker compose up --build
```

Or with Podman:
```bash
podman compose up --build
```

5. Access the application:
   - Frontend: http://localhost:4200
   - Backend API: http://localhost:8080
   - Health check: http://localhost:8080/actuator/health

## Local Development

### Backend

```bash
cd backend
./gradlew bootRun
```

The backend will start on port 8080.

### Frontend

```bash
cd frontend
npm install
npm start
```

The frontend will start on port 4200.

## Project Structure

```
ConfluencePublisher/
├── backend/              # Spring Boot application
│   ├── src/
│   ├── build.gradle.kts
│   └── Dockerfile
├── frontend/             # Angular application
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── data/                 # SQLite database (gitignored)
├── storage/              # File attachments (gitignored)
├── docker-compose.yml    # Docker Compose configuration
├── .env.example          # Environment variables template
└── README.md
```

## Configuration

### Backend Configuration

Configuration is managed through `backend/src/main/resources/application.yml` and environment variables:

- `DATABASE_URL`: SQLite database location
- `ATTACHMENT_DIRECTORY`: Directory for file uploads
- `CONFLUENCE_URL`: Confluence instance URL
- `CONFLUENCE_USERNAME`: Confluence username/email
- `CONFLUENCE_API_TOKEN`: Confluence API token
- `CONFLUENCE_DEFAULT_SPACE`: Default space key
- `CONFLUENCE_PROVIDER_TYPE`: Provider type (confluence-server or confluence-stub)
- `SCHEDULER_INTERVAL_SECONDS`: Scheduler interval

### Frontend Configuration

Configuration is managed through environment files:

- `src/environments/environment.ts` - Development
- `src/environments/environment.prod.ts` - Production

## Building for Production

### Using Docker/Podman

```bash
docker compose up --build
```

### Manual Build

Backend:
```bash
cd backend
./gradlew bootJar
java -jar build/libs/*.jar
```

Frontend:
```bash
cd frontend
npm run build
# Serve the dist/ folder with a web server
```

## API Documentation

Once the backend is running, you can access:

- Health endpoint: http://localhost:8080/actuator/health

## License

See LICENSE file for details.
