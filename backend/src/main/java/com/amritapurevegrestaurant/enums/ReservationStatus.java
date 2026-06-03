package com.amritapurevegrestaurant.enums;

/**
 * Enumeration for the possible states of a reservation.
 */
public enum ReservationStatus {
    /**
     * Reservation has been requested by the customer.
     */
    REQUESTED,

    /**
     * Reservation has been confirmed by the restaurant.
     */
    CONFIRMED,

    /**
     * Reservation was cancelled.
     */
    CANCELLED,

    /**
     * Guests have completed their dining.
     */
    COMPLETED
}