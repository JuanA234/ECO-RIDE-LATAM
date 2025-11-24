package com.unimag.notificationservice.controller;

import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import com.unimag.notificationservice.service.OutboxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/outbox")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Outbox", description = "Notification outbox monitoring")
public class OutboxController {

    private final OutboxService outboxService;

    @GetMapping("/{id}")
    @Operation(summary = "Get outbox entry by ID")
    public Mono<ResponseEntity<OutboxResponseDTO>> getById(@PathVariable Long id) {
        log.info("GET /api/outbox/{}", id);
        return outboxService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all outbox entries")
    public Flux<OutboxResponseDTO> getAll() {
        log.info("GET /api/outbox");
        return outboxService.getAll();
    }

    @PostMapping("/{id}/retry")
    @Operation(summary = "Retry failed notification")
    public Mono<ResponseEntity<OutboxResponseDTO>> retry(@PathVariable Long id) {
        log.info("POST /api/outbox/{}/retry", id);
        return outboxService.retry(id)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete outbox entry")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        log.info("DELETE /api/outbox/{}", id);
        return outboxService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
