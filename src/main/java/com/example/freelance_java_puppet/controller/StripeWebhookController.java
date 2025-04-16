package com.example.freelance_java_puppet.controller;

import com.example.freelance_java_puppet.entity.User;
import com.example.freelance_java_puppet.repository.UserRepository;
import com.example.freelance_java_puppet.service.EmailService;
import com.example.freelance_java_puppet.service.TransactionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
public class StripeWebhookController {

    @Autowired
    private TransactionService transactionService;

    @Value("${webHooksSignature}")
    private String endpointSecret;

    // Global variable
    String userIdStr;
    int userId;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

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
                    userIdStr = session.getMetadata().get("userId");
                    //reconvert the userId to integer
                    int userId1 = Integer.parseInt(userIdStr);
                    handleCheckoutSessionCompleted(session, userId1);
                    break;
                case "payment_intent.succeeded":
                    PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
                    // Handle successful payment intent
                    //retrieve the userId
                     userIdStr = paymentIntent.getMetadata().get("userId");
                    //convert UserId to int
                     userId = Integer.parseInt(userIdStr);
                    System.out.println("Payment succeeded for user: " + userId);
                    handlePaymentIntentSucceeded(paymentIntent, userId);
                    break;
                case "payment_intent.failed":
                   // PaymentIntent failedPaymentIntent = (PaymentIntent) event.getData().getObject();
                    // Handle failed payment
                    System.out.println("Payment failed : " + event.getType());
                    break;
                case "payment_intent.payment_failed":
                   // System.out.println("Failed: " + intent.getId());
                    break;
                case "charge.succeeded":
                    Charge charge = (Charge) event.getData().getObject();
                    System.out.println("Charge Succeeded: " + charge.getId());
                    // Retrieve userId directly from thz charge

                  //  userIdStr =  charge.getMetadata().get("userId");
                    //but to retrieve the paymentIntent Id you can do this
                 //  String charge1 = charge.getPaymentIntent(); // this will return the payment intent Id

                  //  PaymentIntent encore = PaymentIntent.retrieve(charge1);
                //    System.out.println("Retrieve User Id By encore : " + encore.getMetadata().get("userId") );
                    // Get the Payment Intent Object by the charge
                  //  PaymentIntent chargePayment = PaymentIntent.retrieve(charge.getPaymentIntent());

                    // Retrieve userId from PaymentIntent metadata
                    // userIdStr = chargePayment.getMetadata().get("userId");

                    // userId = Integer.parseInt(userIdStr);
                    // You can access other charge properties such as receipt_url
                    System.out.println("Receipt URL ********* : " + charge.getReceiptUrl());
                    handleChargeSuccess(charge);
                    break;
                // Notify the customer that payment failed
                default:
                    // Handle other event types if needed
                    System.out.println("Unhandled event type: " + event.getType());
            }

            return ResponseEntity.ok("Webhook handled");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook handling failed");
        }
    }

    private void handleCheckoutSessionCompleted(Session session, int userId) {
        // Process your business logic when the checkout session is completed
        System.out.println("Checkout session completed with payment status: " + session.getPaymentStatus());
        //System.out.println("Payment succeeded for user: " + userId);
        System.out.println("Session  succeeded : " + session);
    }

    private void handleChargeSuccess(Charge charge) throws StripeException {

        //but to retrieve the paymentIntent Id you can do this
        String paymentIntentId = charge.getPaymentIntent(); // this will return the payment intent Id
        System.out.println("✅ ✅ ✅Charge succeeded ✅ ✅ : " + charge);

        // Retrieve userId from PaymentIntent metadata
        userIdStr = charge.getMetadata().get("userId");

        userId = Integer.parseInt(userIdStr);
        System.out.println("✅ ✅ ✅Charge succeeded with user Id : ✅ ✅ : " + userId);
        // Complete the transaction after the charge is success
         // transactionService.finalizePayment(charge, userId);
        String receiptUrl = charge.getReceiptUrl();
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("user not found"));
        System.out.println(" sending email to : " + user.getEmail() + receiptUrl );
        emailService.sendInvoiceEmail(user.getEmail(), receiptUrl);

    }

    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent, int userId) throws StripeException {
        // Handle payment intent success
        System.out.println("Payment intent succeeded: " + paymentIntent.getId());
        System.out.println("✅ ✅ Payment intent With all Information : " + paymentIntent);
        System.out.println("Payment succeeded for user: " + userId);
        // transactionService.finalizePayment(paymentIntent.getId(), userId);
    }

}
