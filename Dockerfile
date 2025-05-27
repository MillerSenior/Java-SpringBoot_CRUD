# Build stage
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copy Gradle files first for better layer caching
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./

# Set execute permissions for gradlew
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build the application
RUN ./gradlew bootJar --no-daemon -x test

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
