package com.example.freelance_java_puppet.service;

import com.example.freelance_java_puppet.entity.*;
import com.example.freelance_java_puppet.entity.Card;
import com.example.freelance_java_puppet.repository.CardRepository;
import com.example.freelance_java_puppet.repository.HistoryRepository;
import com.example.freelance_java_puppet.repository.TransactionRepository;
import com.example.freelance_java_puppet.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.InvoiceCreateParams;
import com.stripe.param.InvoiceItemCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
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

    @Autowired
    private EmailService emailService;

    @Value("${stripe.currency}")
    private String currency;

    @Autowired
    private CardRepository cardRepository;

    @Value("${stripe.key.secret}")
    private String secretKey;

    // This is the code to test with ionic FrontEnd
    public Map<String, String> processPayment(int userId) throws StripeException {
        Stripe.apiKey = secretKey;

        // ✅ Step 1: Fetch the user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();

        // ✅ Step 2: Fetch the cart
        Card card = user.getCard();
        if (card == null || card.getHistories().isEmpty()) {
            throw new RuntimeException("No items in the cart");
        }

        // ✅ Step 3: Create a PaymentIntent
        int amountInCents = (int) Math.round(card.getTotalPrice() * 100); // Convert to cents

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amountInCents);
        params.put("currency", currency);
        params.put("payment_method_types", Collections.singletonList("card")); // Card payment

        // Create the PaymentIntent
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // ✅ Step 4: Return both paymentIntentId and clientSecret
        Map<String, String> response = new HashMap<>();
        response.put("paymentIntentId", paymentIntent.getId()); // PaymentIntent ID
        response.put("clientSecret", paymentIntent.getClientSecret()); // Client secret for PaymentSheet

        return response;
    }

    // this is for the success url with ionic

    public boolean finalizePayment(String paymentIntentId) throws StripeException {
        Stripe.apiKey = secretKey;

        // ✅ Step 1: Retrieve PaymentIntent from Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        // ✅ Step 2: Check if payment was ALREADY successful
        if (!"succeeded".equals(paymentIntent.getStatus())) {
            throw new RuntimeException("Payment failed. Status: " + paymentIntent.getStatus());
        }

        // ✅ Step 3: Check if the payment was already processed
        Optional<Transaction> transactionOptional = transactionRepository.findByPaymentId(paymentIntentId);
        if (transactionOptional.isPresent()) {
            System.out.println("Payment already processed. Skipping...");
            return true;
        }

        // ✅ Step 4: Move items from cart to order history
        Transaction transaction = new Transaction();
        User user = transaction.getUser();
        Card card = user.getCard();

        if (card == null) {
            throw new RuntimeException("Cart already cleared or not found.");
        }

        // ✅ Step 5: Move items from cart to order history
        List<History> histories = card.getHistories();
        for (History history : histories) {
            user.getHistories().add(history);
            history.getUsers().add(user);
            history.setCard(null);
            historyRepository.save(history);
        }

        // ✅ Step 6: Clear the cart
        user.setCard(null);
        userRepository.save(user);
        cardRepository.delete(card);

        // ✅ Step 7: Send Invoice Email
        Charge charge = Charge.retrieve(paymentIntent.getCharges().getData().get(0).getId());
        String receiptUrl = charge.getReceiptUrl();
        emailService.sendInvoiceEmail(user.getEmail(), receiptUrl);

        // ✅ Step 8: Save Transaction Record
        transaction.setPaymentId(paymentIntentId);
        transaction.setAmount(paymentIntent.getAmount() / 100);
        //transaction.setStatus("Completed");
        transaction.setPaymentType(PaymentType.STRIPE);
        transaction.setCurrency(currency);
        transactionRepository.save(transaction);


        return true;
    }




    // this will just help you test payment with java spring
    public Transaction processPayment1( int userId) throws  StripeException {
        Stripe.apiKey = secretKey;



        // ✅ Step 1: Fetch the user
        // Fetch user by userId
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();

        // ✅ Step 2: Fetch the cart
        // Get the card associated with the user
        Card card = user.getCard();
        if (card == null) {
            throw new RuntimeException("No items in the cart");
        }

        // ✅ Step 3: Create a PaymentIntent
        // Set up payment parameters
        Map<String, Object> params = new HashMap<>();
        int amountInCents = (int) Math.round(card.getTotalPrice() * 100); // Ensures correct rounding
        params.put("amount", amountInCents);
        params.put("currency", currency);
        params.put("payment_method_types", Collections.singletonList("card"));
        params.put("payment_method", "pm_card_jcb"); // This is for testing, replace with dynamic card method if necessary

        // Create the PaymentIntent
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // ✅ Step 4: Confirm payment if necessary
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

        // ✅ Step 5: Handle successful payment
        // If the payment was successful, associate the histories to the user
        if ("succeeded".equals(paymentIntent.getStatus())) {

            // ✅ Step 6: Move all histories to the user
            List<History> histories = card.getHistories();
            if (!histories.isEmpty()) {
                for (History history : histories) {
                    user.getHistories().add(history);
                    history.getUsers().add(user);

                    history.setCard(null);

                    historyRepository.save(history);

                }
            }

            // Retrieve the Charge object associated with the PaymentIntent
            Charge charge = Charge.retrieve(paymentIntent.getCharges().getData().get(0).getId());

            // Extract the receipt URL from the Charge object
            String receiptUrl = charge.getReceiptUrl();

            System.out.println("Receipt URL: " + receiptUrl);

            // ✅ Step 7: Clear the cart and save the transaction
            user.setCard(null);
            userRepository.save(user);
            cardRepository.delete(card);
            // ✅ Step 8: Save the transaction

            Transaction paymentRequest = new Transaction();
            paymentRequest.setUser(user);
            paymentRequest.setAmount(card.getTotalPrice());
            paymentRequest.setCurrency(currency);
            // Save the transaction to the database (you may want to adjust this part)
            paymentRequest.setPaymentId(paymentIntent.getId());
            paymentRequest.setPaymentType(PaymentType.STRIPE);
            transactionRepository.save(paymentRequest);

            // Create the Invoice

           // Invoice invoice = createInvoice(user, card, paymentIntent);
            // ✅ Step 9: Fetch the invoice from Stripe

           // String invoiceUrl = invoice.getHostedInvoiceUrl();
          // String invoiceUrl = getInvoiceUrl(paymentIntent.getId(), user);
            System.out.println("Invoice Created: " + paymentIntent.getId());
            System.out.println("Hosted Invoice URL: " + receiptUrl);
         //   System.out.println("Invoice Status: " + receiptUrl);
            // ✅ Step 10: Send the invoice to the user
            emailService.sendInvoiceEmail(user.getEmail(), receiptUrl);

            return paymentRequest;
        } else {
            throw new RuntimeException("Payment failed. Payment intent status: " + paymentIntent.getStatus());
        }
    }

    // ✅ This method will fetch the Invoice URL from Stripe
    // ✅ This method will fetch the Invoice URL from Stripe
    private String getInvoiceUrl(String paymentIntentId, User user) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        String invoiceId = paymentIntent.getInvoice();

        String customerId = paymentIntent.getCustomer();

        // If the PaymentIntent doesn't have a customer associated, create one (optional)
        if (customerId == null) {
            Customer customer = Customer.create(Map.of(
                    "name", user.getFirstName(),
                    "email", user.getEmail()
            ));
            customerId = customer.getId();  // Use the newly created customer ID
        }

        // Step 1: Retrieve the customer
        Customer customer = Customer.retrieve(customerId);

        // Step 2: Attach the payment method to the customer
        String paymentMethodId = "pm_card_visa";  // Replace with actual payment method ID
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

        // Attach the payment method to the customer
        paymentMethod.attach(PaymentMethodAttachParams.builder().setCustomer(customerId).build());

        // Optionally set it as the default payment method for the customer
        customer = customer.update(Map.of(
                "invoice_settings", Map.of("default_payment_method", paymentMethodId)
        ));

        // Step 3: Create an invoice item for the purchase
        InvoiceItem.create(InvoiceItemCreateParams.builder()
                .setCustomer(customerId)
                .setAmount(paymentIntent.getAmount())  // Amount in cents (e.g., $10.00)
                .setCurrency(currency)
                .setDescription("Product purchase description")
                .build());

        // Step 4: Create the invoice
        InvoiceCreateParams params = InvoiceCreateParams.builder()
                .setCustomer(customerId)
                .setCollectionMethod(InvoiceCreateParams.CollectionMethod.CHARGE_AUTOMATICALLY)  // Automatically charge
                .setAutoAdvance(true)  // Automatically finalize the invoice when payment is successful
                .build();

        Invoice invoice = Invoice.create(params);

        // Step 5: Check if the invoice is finalized
        if (!invoice.getStatus().equals("finalized")) {
            invoice = invoice.finalizeInvoice();  // Finalize if not already done
        }

        // Step 6: If payment is successful, mark as paid
        if ("succeeded".equals(paymentIntent.getStatus())) {
            invoice = invoice.pay();  // Mark as paid
        }

        // Output the hosted invoice URL and status
        System.out.println("Invoice Created: " + invoice.getId());
        System.out.println("Hosted Invoice URL: " + invoice.getHostedInvoiceUrl());
        System.out.println("Invoice Status: " + invoice.getStatus());

        return invoice.getHostedInvoiceUrl();
    }


    private Invoice createInvoice(User user, Card card, PaymentIntent paymentIntent) throws StripeException {
        // ✅ Step 1: Create a Customer (if not already created)
        Customer customer = Customer.create(Map.of(
                "name", user.getFirstName(),
                "email", user.getEmail()
        ));

        // ✅ Step 2: Combine all History names into one string and calculate total amount
        StringBuilder combinedDescription = new StringBuilder();
        int totalAmountInCents = 0;  // Total amount in cents (to be used in the invoice)

        for (History history : card.getHistories()) {
            combinedDescription.append(history.getName()).append(", "); // Add product names to the description
            totalAmountInCents += (int) Math.round(history.getPrice() * 100); // Add the item's price in cents
        }

        // Remove the trailing comma and space from the description string
        if (combinedDescription.length() > 0) {
            combinedDescription.setLength(combinedDescription.length() - 2);
        }

        // ✅ Step 3: Create a single Invoice Item with combined description
        InvoiceItem.create(Map.of(
                "customer", customer.getId(),
                "amount", totalAmountInCents,  // Total amount for all items in the cart (in cents)
                "currency", currency,  // Set your currency here
                "description", combinedDescription.toString()  // Combined list of all history names
               // "quantity", 1  // Only one item, since it's a summary0
                // because .stripe doesn't allow to put in the invoice the amount and quantity in the same we can just specify one;
        ));

        // ✅ Step 4: Create the Invoice
        Invoice invoice = Invoice.create(Map.of(
                "customer", customer.getId(),
              //  "collection_method", "send_invoice",  // This indicates an email invoice and it's useful because stripe will auto send email but for that you will specify
              // this  "days_until_due", daysUntilDue or "due_date",
                "collection_method","charge_automatically",
                "description", "Your purchase from our store",  // Custom description
                "auto_advance", true  // Automatically finalize the invoice when payment is successful
        ));

        // ✅ Step 6: Finalize the invoice (this generates a PDF and prepares it for sending)
        invoice.finalizeInvoice();

        // ✅ Step 7: Return the Invoice Object
        return invoice;
    }

}
