package com.fitlifespa.microservice_gateway.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.context.annotation.Bean;

@Configuration
public class GatewayCorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedOrigin("http://localhost:8081");
        corsConfig.addAllowedOrigin("http://localhost:8082");
        corsConfig.addAllowedOrigin("http://localhost:8083");
        corsConfig.addAllowedOrigin("http://localhost:8084");
        corsConfig.addAllowedOrigin("http://localhost:8085");
        corsConfig.addAllowedOrigin("http://localhost:8086");
        corsConfig.addAllowedOrigin("http://localhost:8087");
        corsConfig.addAllowedOrigin("http://localhost:8088");
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");
        corsConfig.addExposedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}

