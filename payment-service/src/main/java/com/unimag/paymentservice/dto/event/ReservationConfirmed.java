package com.unimag.paymentservice.dto.event;

import lombok.Builder;

@Builder
public record ReservationConfirmed(Long reservationId) {
}
