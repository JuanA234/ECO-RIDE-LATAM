package com.unimag.tripservice.dto.event;

import lombok.Builder;

@Builder
public record PaymentFailed(Long reservationId, String reason) {
}
