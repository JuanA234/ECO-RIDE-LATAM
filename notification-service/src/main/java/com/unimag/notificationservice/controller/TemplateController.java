package com.unimag.notificationservice.controller;

import com.unimag.notificationservice.dto.request.CreateTemplateRequestDTO;
import com.unimag.notificationservice.dto.request.UpdateTemplateRequestDTO;
import com.unimag.notificationservice.dto.response.TemplateResponseDTO;
import com.unimag.notificationservice.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Templates", description = "Template management endpoints")
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/{id}")
    @Operation(summary = "Get template by ID")
    public Mono<ResponseEntity<TemplateResponseDTO>> getById(@PathVariable Long id) {
        log.info("GET /api/v1/templates/{}", id);
        return templateService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all templates")
    public Flux<TemplateResponseDTO> getAll() {
        log.info("GET /api/v1/templates");
        return templateService.getAll();
    }

    @PostMapping
    @Operation(summary = "Create template")
    public Mono<ResponseEntity<TemplateResponseDTO>> create(
            @Valid @RequestBody CreateTemplateRequestDTO request) {
        log.info("POST /api/v1/templates");
        return templateService.create(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update template")
    public Mono<ResponseEntity<TemplateResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTemplateRequestDTO request) {
        log.info("PATCH /api/v1/templates/{}", id);
        return templateService.update(id, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete template")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/templates/{}", id);
        return templateService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

}
