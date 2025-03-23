package com.example.freelance_java_puppet.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
public class StripeWebhookController {

    @Value("${webHooksSignature}")
    private String endpointSecret;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Set your Stripe secret key
            //String endpointSecret = "your-stripe-webhook-signing-secret"; // Replace with your webhook signing secret

            // Construct event from Stripe payload
            Event event = null;
            try {
                event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            } catch (SignatureVerificationException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook signature verification failed");
            }

            // Handle the event based on the event type
            switch (event.getType()) {
                case "checkout.session.completed":
                    Session session = (Session) event.getData().getObject();
                    // Handle successful payment here
                    handleCheckoutSessionCompleted(session);
                    break;
                case "payment_intent.succeeded":
                    PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
                    // Handle successful payment intent
                    handlePaymentIntentSucceeded(paymentIntent);
                    break;
                case "payment_intent.failed":
                   // PaymentIntent failedPaymentIntent = (PaymentIntent) event.getData().getObject();
                    // Handle failed payment
                    System.out.println("Payment failed : " + event.getType());
                    break;
                default:
                    // Handle other event types if needed
                    System.out.println("Unhandled event type: " + event.getType());
            }

            return ResponseEntity.ok("Webhook handled");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook handling failed");
        }
    }

    private void handleCheckoutSessionCompleted(Session session) {
        // Process your business logic when the checkout session is completed
        System.out.println("Checkout session completed with payment status: " + session.getPaymentStatus());
    }

    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {
        // Handle payment intent success
        System.out.println("Payment intent succeeded: " + paymentIntent.getId());
    }

}
