package com.unimag.paymentservice.dto;

import lombok.Builder;

@Builder
public record PaymentAuthorized(Long reservationId, Long paymentId, Long chargeId) {
}
