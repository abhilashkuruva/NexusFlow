package com.nexusflow;

import com.nexusflow.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.nexusflow.repository")
@EntityScan(basePackages = "com.nexusflow.entity")
public class NexusFlowApplication implements CommandLineRunner {

    private final UserService userService;

    public NexusFlowApplication(UserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(NexusFlowApplication.class, args);
    }

    @Override
    public void run(String... args) {
        userService.createDefaultAdminIfNotExists();
        System.out.println("🚀 NexusFlow Backend Started Successfully");
    }
}