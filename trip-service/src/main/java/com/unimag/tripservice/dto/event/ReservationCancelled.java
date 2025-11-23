package com.unimag.tripservice.dto.event;

import lombok.Builder;

@Builder
public record ReservationCancelled(Long resevationId, String reasonq) {
}
