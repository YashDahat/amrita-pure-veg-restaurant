package com.amritapurevegrestaurant.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Service to interact with the Razorpay payment gateway.
 * Handles creation of Razorpay orders and verification of payment signatures.
 */
@Service
@Slf4j
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final String razorpayKeySecret; // Stored for signature verification

    /**
     * Constructor for PaymentService, injecting Razorpay API keys and initializing the Razorpay client.
     *
     * @param razorpayKeyId The public key ID for Razorpay.
     * @param razorpayKeySecret The secret key for Razorpay.
     * @throws IllegalStateException if the Razorpay client cannot be initialized.
     */
    public PaymentService(@Value("${razorpay.key_id}") String razorpayKeyId,
                          @Value("${razorpay.key_secret}") String razorpayKeySecret) {
        this.razorpayKeySecret = razorpayKeySecret; // Store secret for signature verification
        try {
            this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            log.info("RazorpayClient initialized successfully with Key ID: {}", razorpayKeyId);
        } catch (RazorpayException e) {
            log.error("Failed to initialize RazorpayClient: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to initialize Razorpay payment gateway client.", e);
        }
    }

    /**
     * Creates an order on Razorpay and returns the Razorpay Order ID.
     * The amount is converted to the smallest currency unit (e.g., paise for INR).
     *
     * @param amount The total amount of the order in major currency unit (e.g., INR).
     * @param receiptId A unique identifier for the order from our system (e.g., internal order ID).
     * @return The Razorpay Order ID.
     * @throws IllegalStateException if there is an issue communicating with Razorpay.
     */
    public String createRazorpayOrder(BigDecimal amount, String receiptId) {
        try {
            JSONObject orderRequest = new JSONObject();
            // Razorpay expects amount in smallest currency unit (e.g., paise for INR).
            // Multiply by 100 for INR, then convert to long.
            orderRequest.put("amount", amount.multiply(new BigDecimal("100")).longValue());
            orderRequest.put("currency", "INR"); // Assuming INR as per business context
            orderRequest.put("receipt", receiptId);
            orderRequest.put("payment_capture", 1); // Auto capture payment upon successful transaction

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            String razorpayOrderId = razorpayOrder.get("id");
            log.info("Razorpay order created successfully. Order ID: {}, Receipt ID: {}", razorpayOrderId, receiptId);
            return razorpayOrderId;
        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order for receiptId {}: {}", receiptId, e.getMessage(), e);
            throw new IllegalStateException("Failed to create Razorpay order.", e);
        }
    }

    /**
     * Verifies the signature returned by Razorpay webhook to confirm payment authenticity.
     * This is crucial for security to ensure the webhook notification is legitimate.
     *
     * @param razorpayOrderId The order ID from Razorpay.
     * @param razorpayPaymentId The payment ID from Razorpay.
     * @param razorpaySignature The signature received from Razorpay webhook.
     * @return true if the signature is valid, false otherwise.
     */
    public boolean verifyPaymentSignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("razorpay_order_id", razorpayOrderId);
            attributes.put("razorpay_payment_id", razorpayPaymentId);
            attributes.put("razorpay_signature", razorpaySignature);

            boolean isVerified = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
            if (isVerified) {
                log.info("Razorpay payment signature verified successfully for order ID: {}", razorpayOrderId);
            } else {
                log.warn("Razorpay payment signature verification failed for order ID: {}. Invalid signature.", razorpayOrderId);
            }
            return isVerified;
        } catch (RazorpayException e) {
            log.error("Error during Razorpay payment signature verification for order ID {}: {}", razorpayOrderId, e.getMessage(), e);
            // As per the contract, return false on verification failure or exception.
            return false;
        }
    }
}