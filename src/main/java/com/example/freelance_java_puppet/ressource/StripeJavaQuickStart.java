package com.example.freelance_java_puppet.ressource;
/*
import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import static spark.Spark.post;

import static org.springframework.http.RequestEntity.post;

public class StripeJavaQuickStart {
    public static void main(String[] args) {
        String endpointSecret = "whsec_...";

        post("/webhook", (request, response) -> {
            String payload = request.body();
            String sigHeader = request.headers("Stripe-Signature");
            Event event = null;

            try {
                event = Webhook.constructEvent(
                        payload, sigHeader, endpointSecret
                );
            } catch (JsonSyntaxException e) {
                // Invalid payload
                response.status(400);
                return "Invalid payload";
            } catch (SignatureVerificationException e) {
                // Invalid signature
                response.status(400);
                return "Invalid signature";
            }

            PaymentIntent intent = (PaymentIntent) event
                    .getDataObjectDeserializer()
                    .getObject()
                    .get();

            switch(event.getType()) {
                case "payment_intent.succeeded":
                    System.out.println("Succeeded: " + intent.getId());
                    break;
                // Fulfil the customer's purchase

                case "payment_intent.payment_failed":
                    System.out.println("Failed: " + intent.getId());
                    break;
                // Notify the customer that payment failed

                default:
                    // Handle other event types
                    break;
            }

            response.status(200);
            return "OK";
        });
    }
}


 */