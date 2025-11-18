package com.unimag.passengerservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDriverProfileRequestDTO(@NotBlank(message = "Passenger Id is required")
                                            Long passengerId,

                                            @NotBlank(message = "License number is required")
                                            String licenseNo,

                                            @NotBlank(message = "Car plate is required")
                                            String carPlate,

                                            @NotNull(message = "Seats offered is required")
                                            @Min(value = 1, message = "Seats offered must be at least 1")
                                            Integer seatsOffered) {
}
