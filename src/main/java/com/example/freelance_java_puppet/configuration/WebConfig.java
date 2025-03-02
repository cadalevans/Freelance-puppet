package com.example.freelance_java_puppet.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Handle image and audio files
        registry.addResourceHandler("/uploads/**") // This is the URL pattern that will be used to access the files
                .addResourceLocations("file:uploads/"); // File system location of the files
    }
}
