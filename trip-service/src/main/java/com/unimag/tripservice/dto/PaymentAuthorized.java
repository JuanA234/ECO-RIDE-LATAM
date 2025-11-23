package com.unimag.tripservice.dto;

import lombok.Builder;

@Builder
public record PaymentAuthorized(Long reservationId, Long paymentId, Long chargeId) {
}
