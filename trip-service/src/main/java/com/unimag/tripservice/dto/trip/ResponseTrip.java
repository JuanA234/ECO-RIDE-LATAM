package com.unimag.tripservice.dto.trip;

import com.unimag.tripservice.enums.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResponseTrip(Long driverId, String origin, String destination,
                           LocalDateTime startTime, Integer seatsTotal, Integer seatsAvailable,
                           BigDecimal price, TripStatus status) {
}
