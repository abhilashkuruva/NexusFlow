package com.nexusflow.config;

import com.nexusflow.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupConfig {

    @Bean
    public CommandLineRunner init(UserService userService) {
        return args -> {
            // Seed default admin (and verify seeded password hash format)

            userService.createDefaultAdminIfNotExists();

            // Extra diagnostics to quickly identify login failures (email not found vs inactive vs bcrypt mismatch)
            try {
                String adminEmail = "admin@nexusflow.com";
                userService.findByEmail(adminEmail).ifPresentOrElse(user -> {
                    String storedPassword = user.getPassword();
                    System.out.println("[Auth Diagnostics] admin exists: " + adminEmail);
                    System.out.println("[Auth Diagnostics] admin isActive: " + user.getIsActive());
                    boolean looksLikeBCrypt = storedPassword != null && (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$"));
                    System.out.println("[Auth Diagnostics] admin password looksLikeBCrypt: " + looksLikeBCrypt);
                }, () -> {
                    System.out.println("[Auth Diagnostics] admin NOT found: " + adminEmail);
                });
            } catch (Exception e) {
                System.out.println("[Auth Diagnostics] Unable to run auth diagnostics: " + e.getMessage());
            }

            System.out.println("Startup initialization completed");

        };
    }
}