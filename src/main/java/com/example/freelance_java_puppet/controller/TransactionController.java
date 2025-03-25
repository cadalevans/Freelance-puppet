package com.example.freelance_java_puppet.controller;

import com.example.freelance_java_puppet.entity.Transaction;
import com.example.freelance_java_puppet.service.TransactionService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
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


    /*
    ✅ 💯 WHY DID IT WORK ON THE WEB BUT NOT IN THE APP? 💀
👉 💥 Because in Web, Stripe automatically handles payment confirmation via clientSecret.
👉 💥 But in Mobile (Android/iOS), Stripe requires paymentIntentId.

👉 💳 In Mobile Apps, you MUST send:
✅ paymentIntentId (not clientSecret).
✅ That's why it failed ONLY IN THE APP.
     */

}
