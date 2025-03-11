package com.example.freelance_java_puppet.ressource;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:4200",
                        "http://localhost:8100",
                        "http://192.168.1.11:8082" // ✅ Add your device's IP
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS","*")
                .allowedHeaders("Content-Type", "Authorization","X-Requested-With", "observe")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

