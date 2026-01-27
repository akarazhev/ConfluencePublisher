# Build stage
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy Gradle files first for dependency caching
COPY build.gradle.kts settings.gradle.kts ./

# Download dependencies (cached if Gradle files don't change)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src ./src

# Build application (skip tests for faster builds)
RUN gradle build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Install wget for healthcheck
RUN apk add --no-cache wget

WORKDIR /app

# Create directories for data and attachments
RUN mkdir -p /data /storage/attachments

# Copy JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Set environment variables for Docker profile
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8080
ENV APP_DATABASE_URL=jdbc:sqlite:/data/app.db
ENV APP_ATTACHMENT_DIR=/storage/attachments

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
