package com.example.freelance_java_puppet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .disable())
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(
                                // Allow public access to these endpoints
                                "/add-user", "/login", "/resendVerificationEmail/{email}", "/verify-user/{email}/{code}",
                                "/get-user_id/{id}", "/match-password-code/{email}/{code}", "/changePassword/{email}/{password}",
                                "/getAllCategory", "/add-category","/assignCategoryToHistory/{categoryId}/{historyId}",
                                "/add-history", "/user-history/{userId}",
                                "/create-and-add-history-card/{historyId}/{userId}", "/history-by-userCard/{userId}"
                        ).permitAll() // No authentication required for these paths

                        // Require authentication for /pay-history/{userId}
                      //  .requestMatchers("/pay-history/{userId}").authenticated() // Only authenticated users can access

                        // For other requests, allow everyone or require specific roles as needed
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults()); // Configure basic authentication (or you can use another method like JWT)
        return http.build();
    }
}
