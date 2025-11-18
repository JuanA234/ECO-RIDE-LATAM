package com.unimag.passengerservice.dto.request;

import com.unimag.passengerservice.entity.VerificationStatus;
import jakarta.validation.constraints.Min;

public record UpdateDriverProfileRequestDTO(String licenseNumber,
                                            String carPlate,
                                            @Min(value = 1, message = "Seats offered must be at least 1")
                                            Integer seatsOffered,
                                            VerificationStatus verificationStatus) {
}
