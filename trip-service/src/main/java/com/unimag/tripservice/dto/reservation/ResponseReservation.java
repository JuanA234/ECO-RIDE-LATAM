package com.unimag.tripservice.dto.reservation;

import com.unimag.tripservice.enums.ReservationStatus;

import java.time.LocalDateTime;

public record ResponseReservation(Long tripId, Long passengerId, ReservationStatus status, LocalDateTime createdAt) {
}
