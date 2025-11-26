package com.unimag.notificationservice.dto.events;

import java.time.LocalDateTime;
import java.util.List;

public record TripCompletedEvent(
        Long tripId,
        Long driverId,
        String driverEmail,
        String driverName,
        List<PassengerInfo> passengers,
        LocalDateTime completedAt
) {
    public record PassengerInfo(
            Long passengerId,
            String email,
            String name
    ) {}
}
