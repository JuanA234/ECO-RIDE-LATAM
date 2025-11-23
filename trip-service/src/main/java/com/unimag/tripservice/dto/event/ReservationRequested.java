package com.unimag.tripservice.dto.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReservationRequested(Long reservationId, Long tripId, Long passengerId, BigDecimal amo
) {
}
