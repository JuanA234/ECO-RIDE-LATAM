package com.unimag.tripservice.dto;

import lombok.Builder;

@Builder
public record PaymentFailed(Long reservationId, String reason) {
}
