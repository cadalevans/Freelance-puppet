package com.example.freelance_java_puppet.configuration;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class PaypalConfig {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public APIContext apiContext() {
        APIContext apiContext = new APIContext(clientId, clientSecret, mode);
        return apiContext;
    }
}
