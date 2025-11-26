package com.unimag.notificationservice.dto.events;

public record ReservationCancelledEvent(
        Long reservationId,
        Long tripId,
        Long passengerId,
        String passengerEmail,
        String passengerName,
        String reason
) {}
