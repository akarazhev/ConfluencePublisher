# Build Stage
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy Gradle files first for dependency caching
COPY build.gradle.kts settings.gradle.kts ./

# Download dependencies (cached if Gradle files don't change)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src/ ./src/

# Build application (skip tests for faster builds)
RUN gradle build -x test --no-daemon

# Runtime Stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install wget for health checks
RUN apk add --no-cache wget

# Create required directories
RUN mkdir -p /data /storage/attachments

# Copy JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Set environment variables for Docker profile
ENV SPRING_PROFILES_ACTIVE=docker
ENV APP_DATABASE_URL=jdbc:sqlite:/data/app.db
ENV APP_ATTACHMENT_DIR=/storage/attachments

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
