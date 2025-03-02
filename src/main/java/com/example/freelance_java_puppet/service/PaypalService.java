package com.example.freelance_java_puppet.service;

import com.example.freelance_java_puppet.entity.Card;
import com.example.freelance_java_puppet.entity.History;
import com.example.freelance_java_puppet.entity.PaymentType;
import com.example.freelance_java_puppet.entity.User;
import com.example.freelance_java_puppet.repository.CardRepository;
import com.example.freelance_java_puppet.repository.HistoryRepository;
import com.example.freelance_java_puppet.repository.TransactionRepository;
import com.example.freelance_java_puppet.repository.UserRepository;
import com.paypal.api.payments.*;

import com.example.freelance_java_puppet.configuration.PaypalConfig;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaypalService {


    @Autowired
    private APIContext apiContext;
    @Autowired
    private UserRepository userRepository;

    @Value("${paypal.currency}")
    private String currency;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public Payment createPayment(int userId) throws PayPalRESTException {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("user not found"));
        Card card = user.getCard();
        Amount paymentAmount = new Amount();
        paymentAmount.setCurrency(currency);
        paymentAmount.setTotal(String.valueOf(card.getTotalPrice()));

        // This transaction it's a PayPal information; not the transaction class
        Transaction transaction = new Transaction();
        transaction.setAmount(paymentAmount);
        transaction.setDescription("Payment transaction description.");

        Payment requestPayment = new Payment();
        requestPayment.setIntent("sale");
        requestPayment.setTransactions(List.of(transaction));

        // Set the payer information (required field)
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");  // Specify the payment method (e.g., PayPal)
        requestPayment.setPayer(payer);    // Set the payer to the requestPayment

        //redirect URLs for approval and cancellation
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:8082/api/paypal/cancel");
        redirectUrls.setReturnUrl("http://localhost:8082/api/paypal/success"+"?userId="+userId);
        requestPayment.setRedirectUrls(redirectUrls);

        Payment createdPayment = requestPayment.create(apiContext);

        return createdPayment;

    }

    //success in case

    public ResponseEntity<?> paymentSuccess(String paymentId, String payerId, int userId) throws PayPalRESTException{
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        Payment executedPayment = payment.execute(apiContext, paymentExecution);

        // Fetch user by userId
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();

        // Get the card associated with the user
        Card card = user.getCard();

        // Extract transaction details from PayPal's response
        Transaction paypalTransaction = executedPayment.getTransactions().get(0);
        double amount = Double.parseDouble(paypalTransaction.getAmount().getTotal());
        String currency = paypalTransaction.getAmount().getCurrency();
        String description = paypalTransaction.getDescription();

        List<History> histories = card.getHistories();
        if (!histories.isEmpty()) {
            for (History history : histories) {
                user.getHistories().add(history);
                history.getUsers().add(user);

                history.setCard(null);

                historyRepository.save(history);

            }
        }
        // this is my entity Transaction neither nor the PayPal transaction
        com.example.freelance_java_puppet.entity.Transaction transaction = new com.example.freelance_java_puppet.entity.Transaction();
        user.setCard(null);
        transaction.setUser(user);
        userRepository.save(user);

        cardRepository.delete(card);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setPaymentType(PaymentType.PAYPAL);
        transaction.setPaymentId(executedPayment.getId());
        transactionRepository.save(transaction);

        return ResponseEntity.ok().body("Paypal payment successful " + executedPayment.getId());

    }

}
