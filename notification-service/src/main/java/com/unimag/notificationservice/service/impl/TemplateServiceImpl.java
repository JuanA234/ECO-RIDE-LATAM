package com.unimag.notificationservice.service.impl;

import com.unimag.notificationservice.dto.request.CreateTemplateRequestDTO;
import com.unimag.notificationservice.dto.response.TemplateResponseDTO;
import com.unimag.notificationservice.dto.request.UpdateTemplateRequestDTO;
import com.unimag.notificationservice.exception.notfound.TemplateNotFoundException;
import com.unimag.notificationservice.mapper.TemplateMapper;
import com.unimag.notificationservice.repository.TemplateRepository;
import com.unimag.notificationservice.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    TemplateRepository templateRepository;
    TemplateMapper templateMapper;

    @Override
    public Mono<TemplateResponseDTO> getById(Long id) {
        log.debug("Getting template by id: {}", id);

        return templateRepository.findById(id)
                .switchIfEmpty(Mono.error(new TemplateNotFoundException("template not found with id: " + id)))
                .map(templateMapper::toDTO);
    }

    @Override
    public Flux<TemplateResponseDTO> getAll() {
        log.debug("Getting all templates");
        return templateRepository.findAll()
                .map(templateMapper::toDTO);
    }

    @Override
    public Mono<TemplateResponseDTO> create(CreateTemplateRequestDTO request) {
        log.debug("Creating template: {}", request);

        return Mono.just(request)
                .map(templateMapper::toEntity)
                .flatMap(templateRepository::save)
                .map(templateMapper::toDTO);
    }

    @Override
    @Transactional
    public Mono<TemplateResponseDTO> update(Long id, UpdateTemplateRequestDTO request) {
        log.info("Updating template with id: {}", id);

        return templateRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new TemplateNotFoundException("template not found with id: " + id)
                ))
                .map(template -> {
                    if(request.code() != null) template.setCode(request.code());
                    if(request.channel() != null) template.setChannel(request.channel());
                    if(request.subject() != null) template.setSubject(request.subject());
                    if(request.body() != null) template.setBody(request.body());
                    return template;
                })
                .flatMap(templateRepository::save)
                .map(templateMapper::toDTO);
    }

    @Override
    @Transactional
    public Mono<Void> delete(Long id) {
        log.info("Deleting template with id: {}", id);

        return templateRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(
                                new TemplateNotFoundException("template not found with id: " + id)
                        );
                    }
                    return templateRepository.deleteById(id);
                });
    }
}