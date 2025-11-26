package com.unimag.notificationservice.dto.events;

import java.time.LocalDateTime;

public record ReservationConfirmedEvent(
        Long reservationId,
        Long tripId,
        Long passengerId,
        String passengerEmail,
        String passengerName,
        String origin,
        String destination,
        LocalDateTime startTime
) {}
