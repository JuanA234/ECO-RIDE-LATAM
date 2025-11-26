package com.unimag.passengerservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreatePassengerRequestDTO(@NotBlank(message = "Name is required")
                                        String name,

                                        @NotBlank(message = "Email is required")
                                        @Email(message = "Email must be valid")
                                        String email) {
}
