# Build stage
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copy Gradle build scripts and wrapper
COPY gradlew ./
COPY build.gradle settings.gradle ./
COPY gradle gradle/

# Set execute permissions for gradlew
RUN chmod +x ./gradlew

# Pre-download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy the full project
COPY . .

# Build the application
RUN ./gradlew bootJar --no-daemon -x test

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built jar from the builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
