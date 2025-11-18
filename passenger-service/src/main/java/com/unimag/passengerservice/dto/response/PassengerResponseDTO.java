package com.unimag.passengerservice.dto.response;

import java.time.LocalDateTime;

public record PassengerResponseDTO(Long id,
                                   String name,
                                   String email,
                                   Double ratingAvg,
                                   LocalDateTime createdAt) {
}
