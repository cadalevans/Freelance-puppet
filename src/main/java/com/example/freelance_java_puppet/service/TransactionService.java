package com.example.freelance_java_puppet.service;

import com.example.freelance_java_puppet.entity.Card;
import com.example.freelance_java_puppet.entity.History;
import com.example.freelance_java_puppet.entity.Transaction;
import com.example.freelance_java_puppet.entity.User;
import com.example.freelance_java_puppet.repository.CardRepository;
import com.example.freelance_java_puppet.repository.HistoryRepository;
import com.example.freelance_java_puppet.repository.TransactionRepository;
import com.example.freelance_java_puppet.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    public HistoryRepository historyRepository;


    @Value("${stripe.currency}")
    private String currency;

    @Autowired
    private CardRepository cardRepository;

    @Value("${stripe.key.secret}")
    private String secretKey;
    public Transaction processPayment1( int userId) throws StripeException, StripeException {
        Stripe.apiKey = secretKey;

        Transaction paymentRequest = new Transaction();

        // Fetch user by userId
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();

        // Get the card associated with the user
        Card card = user.getCard();

        // Set up payment parameters
        Map<String, Object> params = new HashMap<>();
        int amountInCents = (int) Math.round(card.getTotalPrice() * 100); // Ensures correct rounding
        params.put("amount", amountInCents);
        params.put("currency", currency);
        params.put("payment_method_types", Collections.singletonList("card"));
        params.put("payment_method", "pm_card_visa"); // This is for testing, replace with dynamic card method if necessary

        // Create the PaymentIntent
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Handle payment confirmation based on status
        if ("requires_confirmation".equals(paymentIntent.getStatus())) {
            paymentIntent = paymentIntent.confirm(); // Confirm the payment if it requires confirmation
        }

        // Handle the case where additional actions (e.g., 3D Secure) are required
        if ("requires_action".equals(paymentIntent.getStatus())) {
            // Handle additional authentication actions here, such as redirecting the user for 3D Secure
            // This would require client-side handling (in your front-end) to collect the user's input.
            throw new RuntimeException("Payment requires additional action (e.g., 3D Secure)");
        }

        // If the payment was successful, associate the histories to the user
        if ("succeeded".equals(paymentIntent.getStatus())) {
            List<History> histories = card.getHistories();
            if (!histories.isEmpty()) {
                for (History history : histories) {
                    user.getHistories().add(history);
                    history.getUsers().add(user);

                    history.setCard(null);

                    historyRepository.save(history);

                }
            }

            user.setCard(null);
            paymentRequest.setUser(user);
            userRepository.save(user);
            cardRepository.delete(card);
            paymentRequest.setAmount(card.getTotalPrice());
            paymentRequest.setCurrency(currency);
            // Save the transaction to the database (you may want to adjust this part)
            paymentRequest.setPaymentId(paymentIntent.getId());
            return transactionRepository.save(paymentRequest);
        } else {
            throw new RuntimeException("Payment failed. Payment intent status: " + paymentIntent.getStatus());
        }
    }

}
