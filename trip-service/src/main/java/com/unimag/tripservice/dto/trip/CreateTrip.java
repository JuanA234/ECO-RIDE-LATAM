package com.unimag.tripservice.dto.trip;


import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTrip(
        @NotNull Long driverId,
        @NotNull String origin,
        @NotNull String destination,
        @NotNull LocalDateTime startTime,
        @NotNull Integer seatsTotal,
        @NotNull Integer seatsAvailable,
        @NotNull BigDecimal price) {
}
