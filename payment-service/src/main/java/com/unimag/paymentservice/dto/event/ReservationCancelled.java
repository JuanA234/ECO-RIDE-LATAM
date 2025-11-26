package com.unimag.paymentservice.dto.event;

import lombok.Builder;

@Builder
public record ReservationCancelled(Long reservationId, String reason) {
}
