package com.amritapurevegrestaurant.model;

/**
 * Enum for the status of an order.
 */
public enum OrderStatus {
    /**
     * Order created, awaiting payment.
     */
    PENDING_PAYMENT,

    /**
     * Payment received, order confirmed.
     */
    CONFIRMED,

    /**
     * Kitchen is preparing the order.
     */
    PREPARING,

    /**
     * Order is ready for pickup.
     */
    READY_FOR_PICKUP,

    /**
     * Order has been delivered/picked up.
     */
    DELIVERED,

    /**
     * Order was cancelled.
     */
    CANCELLED
}