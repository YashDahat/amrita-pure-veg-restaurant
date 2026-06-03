package com.amritapurevegrestaurant.service;

import com.amritapurevegrestaurant.dto.CreateReservationRequestDTO;
import com.amritapurevegrestaurant.dto.ReservationResponseDTO;
import com.amritapurevegrestaurant.enums.ReservationStatus;
import com.amritapurevegrestaurant.exception.ReservationConflictException;
import com.amritapurevegrestaurant.model.Reservation;
import com.amritapurevegrestaurant.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing table reservations.
 * Handles business logic related to creating, checking availability, and managing reservation statuses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;

    // Configuration constants for reservation logic
    private static final int MAX_RESTAURANT_CAPACITY = 50; // Total guests the restaurant can accommodate at one time
    private static final int RESERVATION_BLOCK_MINUTES = 90; // How long a reservation effectively blocks capacity (e.g., 1.5 hours)
    private static final int SLOT_DURATION_MINUTES = 30; // Granularity of available slots
    private static final LocalTime RESTAURANT_OPEN_TIME = LocalTime.of(10, 0); // 10:00 AM
    private static final LocalTime RESTAURANT_CLOSE_TIME = LocalTime.of(22, 0); // 10:00 PM (10 PM)

    /**
     * Creates a new table reservation after checking for availability.
     *
     * @param requestDTO The DTO containing reservation details.
     * @return A DTO representing the created reservation.
     * @throws IllegalArgumentException If the reservation time is outside operating hours.
     * @throws ReservationConflictException If there's no availability for the requested time and party size.
     */
    @Transactional
    public ReservationResponseDTO createReservation(CreateReservationRequestDTO requestDTO) {
        log.info("Attempting to create reservation for customer: {} at {} for {} guests",
                requestDTO.getCustomerName(), requestDTO.getReservationTime(), requestDTO.getPartySize());

        LocalDateTime requestedStartTime = requestDTO.getReservationTime();
        LocalDateTime requestedEndTime = requestedStartTime.plusMinutes(RESERVATION_BLOCK_MINUTES);
        LocalTime requestedLocalTime = requestedStartTime.toLocalTime();

        // 1. Check if the requested time is within operating hours and ends before closing
        if (requestedLocalTime.isBefore(RESTAURANT_OPEN_TIME) ||
            requestedEndTime.isAfter(LocalDateTime.of(requestedStartTime.toLocalDate(), RESTAURANT_CLOSE_TIME))) {
            log.warn("Reservation time {} (ends {}) is outside restaurant operating hours ({} - {}) or ends after closing.",
                    requestedLocalTime, requestedEndTime.toLocalTime(), RESTAURANT_OPEN_TIME, RESTAURANT_CLOSE_TIME);
            throw new IllegalArgumentException("Reservation time " + requestedLocalTime + " is outside restaurant operating hours or ends after closing time (" +
                                               RESTAURANT_OPEN_TIME + " - " + RESTAURANT_CLOSE_TIME + ").");
        }

        // 2. Check for capacity conflicts
        int currentBookedCapacity = calculateBookedCapacity(requestedStartTime, requestedEndTime);

        if (currentBookedCapacity + requestDTO.getPartySize() > MAX_RESTAURANT_CAPACITY) {
            log.warn("Reservation conflict: Requested party size {} would exceed max capacity {} at {}. Current booked: {}",
                     requestDTO.getPartySize(), MAX_RESTAURANT_CAPACITY, requestedStartTime, currentBookedCapacity);
            throw new ReservationConflictException("No availability for " + requestDTO.getPartySize() + " guests at " +
                                                   requestedStartTime.toLocalTime() + " on " + requestedStartTime.toLocalDate() +
                                                   ". Please try a different time or smaller party size.");
        }

        // 3. Create and save the reservation
        Reservation reservation = new Reservation();
        reservation.setCustomerName(requestDTO.getCustomerName());
        reservation.setCustomerPhone(requestDTO.getCustomerPhone());
        reservation.setCustomerEmail(requestDTO.getCustomerEmail());
        reservation.setReservationTime(requestedStartTime);
        reservation.setPartySize(requestDTO.getPartySize());
        reservation.setStatus(ReservationStatus.REQUESTED); // Initial status

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Reservation created successfully with ID: {}", savedReservation.getId());

        // 4. Send confirmation notification asynchronously
        notificationService.sendReservationConfirmationEmail(savedReservation);

        return new ReservationResponseDTO(
                savedReservation.getId(),
                savedReservation.getCustomerName(),
                savedReservation.getReservationTime(),
                savedReservation.getPartySize(),
                savedReservation.getStatus().name()
        );
    }

    /**
     * Returns a list of available reservation time slots for a given date.
     * Slots are determined based on restaurant operating hours, reservation block duration,
     * and current confirmed reservations.
     *
     * @param date The date for which to find available slots.
     * @return A list of LocalTime representing available reservation start times.
     */
    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableSlots(LocalDate date) {
        log.info("Fetching available slots for date: {}", date);
        List<LocalTime> availableSlots = new ArrayList<>();

        // Calculate the last possible start time for a reservation to ensure it ends by closing time
        LocalTime lastPossibleStartTime = RESTAURANT_CLOSE_TIME.minusMinutes(RESERVATION_BLOCK_MINUTES);

        if (lastPossibleStartTime.isBefore(RESTAURANT_OPEN_TIME)) {
            log.warn("Reservation block time ({}) is too long for operating hours ({} - {}). No slots might be available.",
                     RESERVATION_BLOCK_MINUTES, RESTAURANT_OPEN_TIME, RESTAURANT_CLOSE_TIME);
            return List.of(); // No slots possible if block time exceeds open hours
        }

        LocalTime currentTime = RESTAURANT_OPEN_TIME;
        while (!currentTime.isAfter(lastPossibleStartTime)) {
            LocalDateTime slotStartTime = LocalDateTime.of(date, currentTime);
            LocalDateTime slotEndTime = slotStartTime.plusMinutes(RESERVATION_BLOCK_MINUTES);

            // For today's date, only consider slots that are in the future
            if (date.isEqual(LocalDate.now()) && slotStartTime.isBefore(LocalDateTime.now())) {
                currentTime = currentTime.plusMinutes(SLOT_DURATION_MINUTES);
                continue;
            }

            int bookedCapacity = calculateBookedCapacity(slotStartTime, slotEndTime);

            // A slot is available if there's capacity for at least one more person
            if (bookedCapacity + 1 <= MAX_RESTAURANT_CAPACITY) {
                availableSlots.add(currentTime);
            }
            currentTime = currentTime.plusMinutes(SLOT_DURATION_MINUTES);
        }
        log.debug("Found {} available slots for date {}", availableSlots.size(), date);
        return availableSlots;
    }

    /**
     * Calculates the total party size of confirmed reservations that overlap with the given time range.
     * A reservation (R_start, R_end) overlaps with (T_start, T_end) if R_start < T_end AND R_end > T_start.
     *
     * @param checkStartTime The start of the time range to check.
     * @param checkEndTime The end of the time range to check.
     * @return The total number of guests from overlapping confirmed reservations.
     */
    private int calculateBookedCapacity(LocalDateTime checkStartTime, LocalDateTime checkEndTime) {
        // Fetch all reservations for the day to perform detailed overlap check
        // Query for reservations that start on the same day as checkStartTime
        LocalDateTime dayStart = checkStartTime.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = checkStartTime.toLocalDate().atTime(LocalTime.MAX);

        List<Reservation> reservationsForDay = reservationRepository.findByReservationTimeBetween(dayStart, dayEnd);

        int bookedCapacity = 0;
        for (Reservation reservation : reservationsForDay) {
            // Only consider CONFIRMED reservations for capacity calculation
            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                LocalDateTime reservationStart = reservation.getReservationTime();
                LocalDateTime reservationEnd = reservationStart.plusMinutes(RESERVATION_BLOCK_MINUTES);

                // Check for overlap: (reservationStart < checkEndTime) AND (reservationEnd > checkStartTime)
                if (reservationStart.isBefore(checkEndTime) && reservationEnd.isAfter(checkStartTime)) {
                    bookedCapacity += reservation.getPartySize();
                }
            }
        }
        return bookedCapacity;
    }
}