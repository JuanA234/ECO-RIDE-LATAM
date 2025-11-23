package com.unimag.tripservice.dto.event;

import lombok.Builder;

@Builder
public record ReservationConfirmed(Long reservationId) {
}
