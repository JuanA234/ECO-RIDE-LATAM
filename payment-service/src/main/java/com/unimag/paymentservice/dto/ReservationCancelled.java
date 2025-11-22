package com.unimag.paymentservice.dto;

import lombok.Builder;

@Builder
public record ReservationCancelled(Long resevationId, String reasonq) {
}
