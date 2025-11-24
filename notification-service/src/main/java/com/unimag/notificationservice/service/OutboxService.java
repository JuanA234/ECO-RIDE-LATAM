package com.unimag.notificationservice.service;


import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OutboxService {
    Mono<OutboxResponseDTO> getById(Long id);
    Flux<OutboxResponseDTO> getAll();
    Mono<OutboxResponseDTO> retry(Long id);
    Mono<Void> delete(Long id);
}
