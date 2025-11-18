package com.unimag.passengerservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRatingRequestDTO(@NotNull(message = "Trip ID is required")
                                     Long tripId,

                                     @NotNull(message = "From passenger ID is required")
                                     Long fromId,

                                     @NotNull(message = "To passenger ID is required")
                                     Long toId,

                                     @NotNull(message = "Score is required")
                                     @Min(value = 1, message = "Score must be between 1 and 5")
                                     @Max(value = 5, message = "Score must be between 1 and 5")
                                     Integer score,

                                     @Size(max = 500, message = "Comment must not exceed 500 characters")
                                     String comment) {
}
