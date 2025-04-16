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
import org.springframework.http.HttpStatus;
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

    @Value("${payment.cancelUrl}")
    private String cancelUrl;

    @Value("${payment.successUrl}")
    private String successUrl;

    public Payment createPayment(int userId) throws PayPalRESTException {
        System.out.println("Paypal Payment begin: ....");
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
       // requestPayment.setIntent("authorize");  please read the note down to use authorize
        requestPayment.setTransactions(List.of(transaction));

        // Set the payer information (required field)
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");  // Specify the payment method (e.g., PayPal)
        requestPayment.setPayer(payer);    // Set the payer to the requestPayment

        //redirect URLs for approval and cancellation
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl (cancelUrl); //("http://localhost:8082/api/paypal/cancel");
        // why do all this ? it's because paypal couldn't redirect to a link that couldn't be accessed publicly
        redirectUrls.setReturnUrl (successUrl + "?userId=" + userId);  //("http://localhost:8082/api/paypal/success"+"?userId="+userId);
        requestPayment.setRedirectUrls(redirectUrls);

        Payment createdPayment = requestPayment.create(apiContext);

        return createdPayment;

    }

    //success in case

    public ResponseEntity<?> paymentSuccess(String paymentId, String payerId, int userId) throws PayPalRESTException {
        // Fetch the existing payment from PayPal (don't create a new one!)
        Payment payment = Payment.get(apiContext, paymentId);

        System.out.println("Executing Payment with ID: " + paymentId);

        // ✅ Check if payment is already completed to avoid duplicate execution
        if ("approved".equalsIgnoreCase(payment.getState())) {
           System.out.print("Payment already completed. No need to execute again.");
        }

        // ✅ If it's not completed, execute it //
        /*
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        Payment executedPayment = payment.execute(apiContext, paymentExecution);

        please just use this if the
        requestPayment.setIntent("authorize"); read the note down
         */

        // Fetch user by userId
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();

        // Get the card associated with the user
        Card card = user.getCard();

        if (card == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No card found for this user.");
        }

        System.out.println("Payer ID: " + payerId);

        // Extract transaction details from PayPal's response
        Transaction paypalTransaction = payment.getTransactions().get(0);
        double amount = Double.parseDouble(paypalTransaction.getAmount().getTotal());
        String currency = paypalTransaction.getAmount().getCurrency();

        List<History> histories = card.getHistories();
        if (!histories.isEmpty()) {
            for (History history : histories) {
                user.getHistories().add(history);
                history.getUsers().add(user);
                history.setCard(null);
                historyRepository.save(history);
            }
        }

        // Save transaction to DB
        com.example.freelance_java_puppet.entity.Transaction transaction = new com.example.freelance_java_puppet.entity.Transaction();
        //userRepository.removeCardFromUser(userId);
        user.setCard(null);
        transaction.setUser(user);
        userRepository.save(user);
        cardRepository.delete(card);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setPaymentType(PaymentType.PAYPAL);
        transaction.setPaymentId(payment.getId());
        transactionRepository.save(transaction);

        return ResponseEntity.ok().body("Paypal payment successful " + payment.getId());
    }

    /*
    Note for paypal,

    When you create a payment with:

requestPayment.setIntent("authorize");
🔹 The payment is NOT executed immediately after approval. Instead, it is only authorized, meaning PayPal checks that the buyer has enough funds but doesn’t charge them yet.

✅ Steps for execution:
User is redirected to PayPal and approves the payment.
PayPal only authorizes the funds (payment is not completed yet).
Your success URL (/api/paypal/success) is called.
Now, you must manually execute the payment with:

PaymentExecution paymentExecution = new PaymentExecution();
paymentExecution.setPayerId(payerId);
Payment executedPayment = payment.execute(apiContext, paymentExecution);
     */

}
