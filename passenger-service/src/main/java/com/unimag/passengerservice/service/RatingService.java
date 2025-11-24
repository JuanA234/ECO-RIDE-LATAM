package com.unimag.passengerservice.service;

import com.unimag.passengerservice.dto.request.CreateRatingRequestDTO;
import com.unimag.passengerservice.dto.response.RatingResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RatingService {
    Mono<RatingResponseDTO> getById(Long id);
    Flux<RatingResponseDTO> getAll();
    Mono<RatingResponseDTO> create(CreateRatingRequestDTO request);
}
