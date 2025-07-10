package com.fitlifespa.microservice_auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Autenticación - FitLifeSpa")
                        .description("Microservicio de autenticación: login y registro de usuarios")
                        .version("1.0.0"));
    }
}

