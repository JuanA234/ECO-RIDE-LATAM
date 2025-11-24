package com.unimag.passengerservice.service;

import com.unimag.passengerservice.dto.request.CreateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.request.UpdateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.response.DriverProfileResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DriverProfileService {
    Mono<DriverProfileResponseDTO> getById(Long id);
    Flux<DriverProfileResponseDTO> getAll();
    Mono<DriverProfileResponseDTO> create(CreateDriverProfileRequestDTO request);
    Mono<DriverProfileResponseDTO> update(Long id, UpdateDriverProfileRequestDTO request);
    Mono<Void> delete(Long id);
}
