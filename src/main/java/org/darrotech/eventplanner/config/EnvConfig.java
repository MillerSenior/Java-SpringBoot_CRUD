package org.darrotech.eventplanner.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:.env")
public class EnvConfig {
    // This class enables Spring Boot to read properties from .env file
} 