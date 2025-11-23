package com.unimag.paymentservice.dto;

import lombok.Builder;

@Builder
public record PaymentFailed(Long reservationId, String reason) {
}
