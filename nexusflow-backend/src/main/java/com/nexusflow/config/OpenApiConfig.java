package com.nexusflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * 
 * Configures Swagger UI with project information, security schemes,
 * and server definitions.
 * 
 * @author NexusFlow Team
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI bean with project metadata and security.
     */
    @Bean
    public OpenAPI nexusFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NexusFlow API")
                        .description("Autonomous AI Supply Chain Risk Intelligence System API Documentation")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("NexusFlow Team")
                                .email("support@nexusflow.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Local development server")
                ))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token in the format: Bearer <token>")));
    }
}