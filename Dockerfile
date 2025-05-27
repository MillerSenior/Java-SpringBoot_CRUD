# Build stage
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copy only the files needed for dependency resolution
COPY build.gradle settings.gradle ./
COPY gradle gradle/
COPY gradlew ./

# Download dependencies
RUN chmod +x gradlew && \
    ./gradlew dependencies --no-daemon

# Copy the rest of the source code
COPY . .

# Build the application
RUN ./gradlew bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create a non-privileged user
RUN adduser --disabled-password --gecos "" --home "/nonexistent" \
    --shell "/sbin/nologin" --no-create-home --uid 1001 appuser \
    && mkdir -p /app/data \
    && chown -R appuser:appuser /app

# Copy the built artifact from builder stage
COPY --from=builder --chown=appuser:appuser /app/build/libs/*.jar app.jar

# Use non-root user
USER appuser

# Environment variables with defaults
ENV DB_USERNAME=sa \
    DB_PASSWORD= \
    SPRING_PROFILES_ACTIVE=prod \
    SPRING_DATASOURCE_URL=jdbc:h2:file:/app/data/event_planner

# Expose the application port
EXPOSE 8080

# Start the application with proper memory settings
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"] 