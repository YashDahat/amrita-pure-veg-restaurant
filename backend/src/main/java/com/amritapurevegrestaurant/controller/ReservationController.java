package com.amritapurevegrestaurant.controller;

import com.amritapurevegrestaurant.dto.CreateReservationRequestDTO;
import com.amritapurevegrestaurant.dto.ReservationResponseDTO;
import com.amritapurevegrestaurant.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * REST controller for reservation-related endpoints.
 * Handles HTTP requests for creating new reservations and fetching available time slots.
 */
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Handles new table reservation requests.
     *
     * @param requestDTO The DTO containing the details for the new reservation.
     * @return A ResponseEntity containing the created ReservationResponseDTO and HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(@Valid @RequestBody CreateReservationRequestDTO requestDTO) {
        log.info("Received request to create reservation for customer: {} at {}", requestDTO.getCustomerName(), requestDTO.getReservationTime());
        ReservationResponseDTO responseDTO = reservationService.createReservation(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Handles requests for available reservation slots for a given date.
     *
     * @param date The date for which to retrieve available slots, formatted as 'YYYY-MM-DD'.
     * @return A ResponseEntity containing a list of available LocalTime slots and HTTP status 200 (OK).
     */
    @GetMapping("/slots")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Received request to get available reservation slots for date: {}", date);
        List<LocalTime> availableSlots = reservationService.getAvailableSlots(date);
        return ResponseEntity.ok(availableSlots);
    }
}