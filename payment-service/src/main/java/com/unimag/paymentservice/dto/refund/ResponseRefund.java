package com.unimag.paymentservice.dto.refund;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ResponseRefund(Long id,
                             Long chargeId,
                             BigDecimal amount,
                             String reason,
                             LocalDateTime createdAt) {
}
