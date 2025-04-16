package com.example.freelance_java_puppet.controller;

import com.example.freelance_java_puppet.service.PaypalService;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
public class PaypalController {

    @Autowired
    private PaypalService payPalService;

    @PostMapping("/create-payment/{userId}")
    public Map<String, String> createPayment(@PathVariable("userId") int userId) throws PayPalRESTException{
        // Create payment and get the PayPal approval URL
        Payment payment = payPalService.createPayment(userId);

        // Find the approval URL from PayPal's response
        String approvalUrl = payment.getLinks().stream()
                .filter(link -> link.getRel().equals("approval_url"))
                .findFirst()
                .map(link -> link.getHref())
                .orElseThrow(() -> new PayPalRESTException("Approval URL not found"));

        // Return the approval URL to the frontend (Ionic or any frontend application)
       // return ResponseEntity.ok(approvalUrl);
        Map<String, String> response = new HashMap<>();
        response.put("approvalUrl", approvalUrl);
        return response;
    }

    @GetMapping("/success")
    public ResponseEntity<?> paymentSuccess(@RequestParam("paymentId") String paymentId,
                                            @RequestParam("PayerID") String payerId,
                                            @RequestParam("token") String token, // You must include token as well
                                            @RequestParam("userId") int userId
                                            ) throws PayPalRESTException {

        // Execute the payment and handle the success case
         payPalService.paymentSuccess(paymentId, payerId, userId);

        // Return a beautiful HTML success page
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

    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel() {
        // If the user cancels the payment, this URL will be called
       // return ResponseEntity.ok("Payment was canceled.");

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


}
