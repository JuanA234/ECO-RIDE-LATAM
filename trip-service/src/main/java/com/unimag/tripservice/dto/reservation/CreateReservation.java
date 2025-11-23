package com.unimag.tripservice.dto.reservation;

import jakarta.validation.constraints.NotNull;

public record CreateReservation(
        @NotNull Long tripId,
        @NotNull Long passengerId) {
}
