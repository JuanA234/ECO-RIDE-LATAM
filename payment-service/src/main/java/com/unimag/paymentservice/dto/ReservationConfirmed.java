package com.unimag.paymentservice.dto;

import lombok.Builder;

@Builder
public record ReservationConfirmed(Long reservationId) {
}
