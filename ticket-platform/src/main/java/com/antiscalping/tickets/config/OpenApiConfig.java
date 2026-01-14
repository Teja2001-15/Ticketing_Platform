package com.antiscalping.tickets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Anti-Scalping Ticket Platform API")
                .version("1.0.0")
                .description("MVP Monolith API for ticketing platform with anti-scalping features")
                .contact(new Contact()
                    .name("Support")
                    .email("support@antiscalping.com")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .getComponents()
                .addSecuritySchemes("Bearer Authentication",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token for authentication"))
                .and()
            .getOpenApi();
    }
}
