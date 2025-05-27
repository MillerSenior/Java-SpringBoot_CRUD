package org.darrotech.eventplanner.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "file:.env", ignoreResourceNotFound = true)
public class EnvConfig {
    // This class enables Spring Boot to read properties from .env file or environment variables
}
