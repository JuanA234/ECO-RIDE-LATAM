package com.unimag.passengerservice.dto.response;

public record RatingResponseDTO(Long id,
                                Long tripId,
                                Long fromId,
                                Long toId,
                                Integer score,
                                String comment) {
}
