package com.unimag.passengerservice.controller;

import com.unimag.passengerservice.dto.request.CreatePassengerRequestDTO;
import com.unimag.passengerservice.dto.request.UpdatePassengerRequestDTO;
import com.unimag.passengerservice.dto.response.PassengerResponseDTO;
import com.unimag.passengerservice.service.PassengerService;
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
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Passengers", description = "Passenger management endpoints")
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping("/{id}")
    @Operation(summary = "Get passenger by ID")
    public Mono<ResponseEntity<PassengerResponseDTO>> getById(@PathVariable Long id) {
        log.info("GET /api/passengers/{}", id);
        return passengerService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all passengers")
    public Flux<PassengerResponseDTO> getAll() {
        log.info("GET /api/passengers");
        return passengerService.getAll();
    }

    @PostMapping
    @Operation(summary = "Create passenger")
    public Mono<ResponseEntity<PassengerResponseDTO>> create(@Valid @RequestBody CreatePassengerRequestDTO request) {
        log.info("POST /api/passengers/");
        return passengerService.create(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update passenger")
    public Mono<ResponseEntity<PassengerResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody UpdatePassengerRequestDTO request) {
        log.info("PATCH /api/passengers/{}", id);
        return passengerService.update(id, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete passenger")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        log.info("DELETE /api/passengers/{}", id);
        return passengerService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
