package com.unimag.passengerservice.service;

import com.unimag.passengerservice.dto.request.CreatePassengerRequestDTO;
import com.unimag.passengerservice.dto.request.UpdatePassengerRequestDTO;
import com.unimag.passengerservice.dto.response.PassengerResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PassengerService {
    Mono<PassengerResponseDTO> getById(Long id);
    Flux<PassengerResponseDTO> getAll();
    Mono<PassengerResponseDTO> create(CreatePassengerRequestDTO request);
    Mono<PassengerResponseDTO> update(Long id, UpdatePassengerRequestDTO request);
    Mono<Void> delete(Long id);
}
