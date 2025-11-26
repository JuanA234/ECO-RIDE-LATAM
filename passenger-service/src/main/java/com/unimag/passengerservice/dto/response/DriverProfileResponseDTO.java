package com.unimag.passengerservice.dto.response;

import com.unimag.passengerservice.entity.enums.VerificationStatus;

public record DriverProfileResponseDTO(Long id,
                                       Long passengerId,
                                       String licenseNumber,
                                       String carPlate,
                                       Integer seatsOffered,
                                       VerificationStatus verificationStatus) {
}
