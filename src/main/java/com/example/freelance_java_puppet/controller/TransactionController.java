package com.example.freelance_java_puppet.controller;

import com.example.freelance_java_puppet.entity.Transaction;
import com.example.freelance_java_puppet.service.TransactionService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("**")
@RequestMapping("/api/stripe")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    /*
    @PostMapping("/transaction/{userId}")
    public Transaction processStripePayment(@PathVariable("userId")int userId) throws StripeException {
        return transactionService.processPayment1(userId);
    }


     */
    //this is the code to test with ionic frontend

    @PostMapping("/transactions/{userId}")
    public ResponseEntity<Map<String, String>> processStripePayment1(@PathVariable("userId") int userId
                                                                   //  @PathVariable("clientType") String clientType
    ) throws StripeException {
        Map<String, String> response = transactionService.processPayment(userId);
        return ResponseEntity.ok(response);
    }


    //success url after payment

    @PostMapping("/payment-success/{charge}/{userId}")
    public ResponseEntity<?> handleSuccessfulPayment(@PathVariable Charge charge, @PathVariable int userId) throws StripeException {
        boolean success = transactionService.finalizePayment(charge, userId);
        if (success) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Payment successfully processed."));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Payment processing failed."));
        }
    }


    @GetMapping("/stripe/success")
    public ResponseEntity<String> stripeSuccessPage() {
        String htmlResponse = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Payment Successful</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; text-align: center; background-color: #f4f4f4; padding: 50px; }" +
                ".container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1); max-width: 400px; margin: auto; }" +
                ".success-icon { font-size: 50px; color: #28a745; margin-bottom: 10px; }" +
                "h1 { color: #333; }" +
                "p { color: #666; font-size: 16px; }" +
                ".button { display: inline-block; background-color: #007bff; color: white; padding: 12px 20px; margin-top: 20px; text-decoration: none; font-size: 16px; border-radius: 5px; transition: background 0.3s; }" +
                ".button:hover { background-color: #0056b3; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='success-icon'>✅</div>" +
                "<h1>Payment Successful!</h1>" +
                "<p>Thank you for your payment. Your transaction has been completed successfully.</p>" +
                "<p>Please manually close this page to return to the app.</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/html").body(htmlResponse);
    }

    @GetMapping("/stripe/cancel")
    public ResponseEntity<String> stripeCancelPage() {
        String htmlResponse = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Payment Cancelled</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; text-align: center; background-color: #f4f4f4; padding: 50px; }" +
                ".container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1); max-width: 400px; margin: auto; }" +
                ".cancel-icon { font-size: 50px; color: #dc3545; margin-bottom: 10px; }" +
                "h1 { color: #333; }" +
                "p { color: #666; font-size: 16px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='cancel-icon'>❌</div>" +
                "<h1>Payment Cancelled</h1>" +
                "<p>Your payment has been cancelled. You can try again anytime.</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/html").body(htmlResponse);
    }


    /*
    ✅ 💯 WHY DID IT WORK ON THE WEB BUT NOT IN THE APP? 💀
👉 💥 Because in Web, Stripe automatically handles payment confirmation via clientSecret.
👉 💥 But in Mobile (Android/iOS), Stripe requires paymentIntentId.

👉 💳 In Mobile Apps, you MUST send:
✅ paymentIntentId (not clientSecret).
✅ That's why it failed ONLY IN THE APP.
     */

}
