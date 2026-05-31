package com.amritapurevegrestaurant.repository;

import com.amritapurevegrestaurant.model.Reservation;
import com.amritapurevegrestaurant.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    /**
     * Finds all confirmed reservations within a given time window.
     *
     * @param start The start of the time window (inclusive).
     * @param end The end of the time window (inclusive).
     * @return A list of confirmed reservations within the specified time range.
     */
    @Query("SELECT r FROM Reservation r WHERE r.reservationTime BETWEEN :start AND :end AND r.status = com.amritapurevegrestaurant.model.ReservationStatus.CONFIRMED")
    List<Reservation> findReservationsForTimeRange(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);
}