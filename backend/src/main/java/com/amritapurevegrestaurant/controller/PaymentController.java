package com.amritapurevegrestaurant.controller;

import com.amritapurevegrestaurant.exception.ResourceNotFoundException;
import com.amritapurevegrestaurant.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling payment webhooks, specifically from Razorpay.
 * This controller receives notifications from Razorpay about payment status changes
 * and delegates the processing to the {@link OrderService}.
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final OrderService orderService;
    private final ObjectMapper objectMapper; // Used for parsing JSON webhook payload

    /**
     * Handles webhook notifications from Razorpay to confirm payments.
     * This endpoint is called by Razorpay when a payment status changes (e.g., captured).
     * It parses the incoming payload, extracts necessary payment details, and
     * calls the {@link OrderService} to verify the payment and update the order status.
     *
     * @param payload The raw JSON payload from Razorpay, containing payment and order details.
     * @param signature The X-Razorpay-Signature header, used to verify the authenticity of the webhook.
     * @return ResponseEntity indicating success (200 OK) or failure (400 Bad Request, 401 Unauthorized, 500 Internal Server Error).
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        log.info("Received Razorpay webhook. Signature: {}", signature);
        log.debug("Webhook payload: {}", payload);

        try {
            // Parse the JSON payload to extract Razorpay Order ID and Payment ID
            JsonNode rootNode = objectMapper.readTree(payload);
            JsonNode paymentEntity = rootNode.path("payload").path("payment").path("entity");

            if (paymentEntity.isMissingNode()) {
                log.error("Invalid Razorpay webhook payload: 'payload.payment.entity' is missing or malformed.");
                return ResponseEntity.badRequest().build();
            }

            String razorpayPaymentId = paymentEntity.path("id").asText();
            String razorpayOrderId = paymentEntity.path("order_id").asText();

            if (razorpayPaymentId.isEmpty() || razorpayOrderId.isEmpty()) {
                log.error("Invalid Razorpay webhook payload: 'id' or 'order_id' missing from payment entity. " +
                        "Received Payment ID: '{}', Order ID: '{}'", razorpayPaymentId, razorpayOrderId);
                return ResponseEntity.badRequest().build();
            }

            // Delegate to the OrderService to confirm the payment and update order status
            orderService.confirmOrderPayment(razorpayOrderId, razorpayPaymentId, signature);

            log.info("Razorpay webhook processed successfully for Razorpay Order ID: {}", razorpayOrderId);
            return ResponseEntity.ok().build();

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Razorpay webhook payload: {}", e.getMessage());
            return ResponseEntity.badRequest().build(); // Malformed JSON payload
        } catch (ResourceNotFoundException e) {
            log.error("Error processing Razorpay webhook: Order not found in our system. Details: {}", e.getMessage());
            // Return 400 Bad Request if the order ID from Razorpay does not correspond to an existing order.
            return ResponseEntity.badRequest().build();
        } catch (SecurityException e) {
            log.error("Error processing Razorpay webhook: Signature verification failed. Details: {}", e.getMessage());
            // Return 401 Unauthorized for signature verification failures.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("An unexpected error occurred while processing Razorpay webhook: {}", e.getMessage(), e);
            // Catch all other unexpected exceptions and return 500 Internal Server Error.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}