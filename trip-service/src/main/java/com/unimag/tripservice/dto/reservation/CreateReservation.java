package com.unimag.tripservice.dto.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateReservation(
        @NotNull Long tripId,
        @NotNull Long passengerId) {
}
