package com.amritapurevegrestaurant.enums;

/**
 * Enumeration for the possible states of an order.
 */
public enum OrderStatus {
    /**
     * Order created, awaiting payment.
     */
    PENDING_PAYMENT,

    /**
     * Payment received, order is confirmed.
     */
    CONFIRMED,

    /**
     * Order is being prepared in the kitchen.
     */
    PREPARING,

    /**
     * Order is ready for pickup.
     */
    READY_FOR_PICKUP,

    /**
     * Order has been picked up/delivered.
     */
    COMPLETED,

    /**
     * Order was cancelled.
     */
    CANCELLED
}