package com.unimag.tripservice.dto;

import lombok.Builder;

@Builder
public record ReservationCancelled(Long resevationId, String reasonq) {
}
