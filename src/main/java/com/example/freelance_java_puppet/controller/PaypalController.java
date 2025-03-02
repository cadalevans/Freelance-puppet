package com.example.freelance_java_puppet.controller;

import com.example.freelance_java_puppet.service.PaypalService;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paypal")
public class PaypalController {

    @Autowired
    private PaypalService payPalService;

    @PostMapping("/create-payment/{userId}")
    public ResponseEntity<String> createPayment(@PathVariable("userId") int userId) throws PayPalRESTException{
        // Create payment and get the PayPal approval URL
        Payment payment = payPalService.createPayment(userId);

        // Find the approval URL from PayPal's response
        String approvalUrl = payment.getLinks().stream()
                .filter(link -> link.getRel().equals("approval_url"))
                .findFirst()
                .map(link -> link.getHref())
                .orElseThrow(() -> new PayPalRESTException("Approval URL not found"));

        // Return the approval URL to the frontend (Ionic or any frontend application)
        return ResponseEntity.ok(approvalUrl);
    }

    @GetMapping("/success")
    public ResponseEntity<?> paymentSuccess(@RequestParam("paymentId") String paymentId,
                                                 @RequestParam("PayerID") String payerId,
                                            @RequestParam("token") String token, // You must include token as well
                                                 @RequestParam("userId") int userId) throws PayPalRESTException {
        // Execute the payment and handle the success case
        return payPalService.paymentSuccess(paymentId, payerId, userId);
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel() {
        // If the user cancels the payment, this URL will be called
        return ResponseEntity.ok("Payment was canceled.");
    }

}
