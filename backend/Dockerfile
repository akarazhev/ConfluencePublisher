# Build Stage
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy Gradle files first for dependency caching
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Download dependencies
RUN gradle dependencies --no-daemon

# Copy source code
COPY src ./src

# Build application
RUN gradle build -x test --no-daemon

# Runtime Stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create necessary directories
RUN mkdir -p /data /storage/attachments

# Copy JAR from build stage
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# Set environment variables for Docker profile
ENV SPRING_PROFILES_ACTIVE=docker

# Expose port
EXPOSE 8080

# Entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]
