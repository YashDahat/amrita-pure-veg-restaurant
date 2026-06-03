package com.amritapurevegrestaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    /**
     * The unique ID of the created order in our system.
     */
    private UUID orderId;

    /**
     * The initial status of the order (e.g., PENDING_PAYMENT).
     */
    private String status;

    /**
     * The total amount to be paid.
     */
    private BigDecimal totalAmount;

    /**
     * The order ID from Razorpay to be used by the frontend.
     */
    private String razorpayOrderId;

    /**
     * The public Razorpay API key for the frontend.
     */
    private String razorpayApiKey;
}