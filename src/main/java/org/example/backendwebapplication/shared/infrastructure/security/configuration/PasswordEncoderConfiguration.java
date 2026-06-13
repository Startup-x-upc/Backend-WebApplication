package org.example.backendwebapplication.shared.infrastructure.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Provides the application-wide PasswordEncoder bean.
 * Uses BCrypt hashing for secure password storage.
 */
@Configuration
public class PasswordEncoderConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
