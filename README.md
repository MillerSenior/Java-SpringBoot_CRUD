# Event Planner Application

A Spring Boot application for event planning and management.

## Setup Instructions

### Environment Configuration

1. Create a `.env` file in the root directory based on the `env/env-example` file:
   ```
   # Database Configuration
   DB_USERNAME=sa
   DB_PASSWORD=
   ```

2. The application uses H2 database (file-based) that stores data in the `./data` directory.

### Running the Application

1. Build the application:
   ```
   ./gradlew build
   ```

2. Run the application:
   ```
   ./gradlew bootRun
   ```

3. Access the application at: http://localhost:8080

4. Access the H2 Console at: http://localhost:8080/h2-console
   - JDBC URL: jdbc:h2:file:./data/event_planner
   - Username: sa (or value from .env)
   - Password: (value from .env)

## Application Changes

This application has been updated to use:
1. H2 Database (file-based) for local storage instead of MySQL
2. Environment variables through .env file for configuration

These changes allow for easier local development without requiring a MySQL server while maintaining the same model functionality. 