package com.nexusflow.config;

import com.nexusflow.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupConfig {

    @Bean
    public CommandLineRunner init(UserService userService, DemoDataSeeder demoDataSeeder) {
        return args -> {
            // Seed default admin
            userService.createDefaultAdminIfNotExists();

            // Seed demo manager and analyst accounts
            userService.createDemoUserIfNotExists(
                    "manager@nexusflow.com", "admin123",
                    "Supply", "Manager", com.nexusflow.entity.User.Role.SUPPLY_MANAGER
            );
            userService.createDemoUserIfNotExists(
                    "logistics@nexusflow.com", "admin123",
                    "Fleet", "Logistics", com.nexusflow.entity.User.Role.LOGISTICS_MANAGER
            );
            userService.createDemoUserIfNotExists(
                    "analyst@nexusflow.com", "admin123",
                    "Risk", "Analyst", com.nexusflow.entity.User.Role.ANALYST
            );
            userService.createDemoUserIfNotExists(
                    "supplier@nexusflow.com", "admin123",
                    "Apex", "Supplier", com.nexusflow.entity.User.Role.SUPPLIER
            );

            demoDataSeeder.seedDemoData();

            // Extra diagnostics
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
            System.out.println("Demo accounts: admin@nexusflow.com, manager@nexusflow.com, analyst@nexusflow.com (all: admin123)");
        };
    }
}
