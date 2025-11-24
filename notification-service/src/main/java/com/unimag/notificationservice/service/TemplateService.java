package com.unimag.notificationservice.service;

import com.unimag.notificationservice.dto.request.CreateTemplateRequestDTO;
import com.unimag.notificationservice.dto.response.TemplateResponseDTO;
import com.unimag.notificationservice.dto.request.UpdateTemplateRequestDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TemplateService {
    Mono<TemplateResponseDTO> getById(Long id);
    Flux<TemplateResponseDTO> getAll();
    Mono<TemplateResponseDTO> create(CreateTemplateRequestDTO request);
    Mono<TemplateResponseDTO> update(Long id, UpdateTemplateRequestDTO request);
    Mono<Void> delete(Long id);
}
