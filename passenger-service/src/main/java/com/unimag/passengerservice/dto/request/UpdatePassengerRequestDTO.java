package com.unimag.passengerservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdatePassengerRequestDTO(String name,
                                        @Email(message = "Email must be valid")
                                        String email) {
}
