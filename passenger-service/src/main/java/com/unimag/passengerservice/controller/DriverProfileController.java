package com.unimag.passengerservice.controller;

import com.unimag.passengerservice.dto.request.CreateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.request.UpdateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.response.DriverProfileResponseDTO;
import com.unimag.passengerservice.service.DriverProfileService;
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
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Drivers", description = "DriverProfile management endpoints")
public class DriverProfileController {
    DriverProfileService driverProfileService;

    @GetMapping("/{id}")
    @Operation(summary = "Get driverProfile by ID")
    public Mono<ResponseEntity<DriverProfileResponseDTO>> getById(@PathVariable Long id) {
        log.info("GET /api/drivers/{}", id);
        return driverProfileService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all drivers")
    public Flux<DriverProfileResponseDTO> getAll() {
        log.info("GET /api/drivers");
        return driverProfileService.getAll();
    }

    @PostMapping("/profile")
    @Operation(summary = "Create driverProfile")
    public Mono<ResponseEntity<DriverProfileResponseDTO>> create(@Valid @RequestBody CreateDriverProfileRequestDTO request) {
        log.info("POST /api/drivers/profile");
        return driverProfileService.create(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update driverProfile")
    public Mono<ResponseEntity<DriverProfileResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody UpdateDriverProfileRequestDTO request) {
        log.info("PATCH /api/drivers/{}", id);
        return driverProfileService.update(id, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete driverProfile")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        log.info("DELETE /api/drivers/{}", id);
        return driverProfileService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }


}
