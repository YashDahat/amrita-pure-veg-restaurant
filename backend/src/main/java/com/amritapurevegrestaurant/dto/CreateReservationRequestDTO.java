package com.amritapurevegrestaurant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequestDTO {

    /**
     * Customer's name.
     */
    @NotBlank(message = "Customer name cannot be blank")
    private String customerName;

    /**
     * Customer's phone number.
     */
    @NotBlank(message = "Customer phone number cannot be blank")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format. Must be 10-15 digits, optionally starting with '+'.")
    private String customerPhone;

    /**
     * Customer's email address.
     */
    @NotBlank(message = "Customer email cannot be blank")
    @Email(message = "Invalid email address format")
    private String customerEmail;

    /**
     * Requested date and time for the reservation.
     */
    @NotNull(message = "Reservation time cannot be null")
    @Future(message = "Reservation time must be in the future")
    private LocalDateTime reservationTime;

    /**
     * Number of guests.
     */
    @Min(value = 1, message = "Party size must be at least 1")
    private int partySize;
}