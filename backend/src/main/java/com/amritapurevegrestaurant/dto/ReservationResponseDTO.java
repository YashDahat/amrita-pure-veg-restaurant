package com.amritapurevegrestaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {
    /**
     * The unique ID of the created reservation.
     */
    private UUID reservationId;

    /**
     * Customer's name.
     */
    private String customerName;

    /**
     * The confirmed date and time of the reservation.
     */
    private LocalDateTime reservationTime;

    /**
     * The number of guests.
     */
    private int partySize;

    /**
     * The initial status of the reservation (e.g., REQUESTED).
     */
    private String status;
}