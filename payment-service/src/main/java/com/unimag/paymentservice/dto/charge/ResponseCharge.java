package com.unimag.paymentservice.dto.charge;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResponseCharge(Long id,
                             Long paymentIntentId,
                             String provider,
                             String providerRef,
                             LocalDateTime createdAt) {
}
