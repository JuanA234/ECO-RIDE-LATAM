package com.unimag.passengerservice.controller;

import com.unimag.passengerservice.dto.request.CreateRatingRequestDTO;
import com.unimag.passengerservice.dto.response.RatingResponseDTO;
import com.unimag.passengerservice.service.RatingService;
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
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ratings", description = "Ratings management endpoints")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/{id}")
    @Operation(summary = "Get rating by ID")
    public Mono<ResponseEntity<RatingResponseDTO>> getById(@PathVariable Long id) {
        log.info("GET /api/ratings/{}", id);
        return ratingService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all ratings")
    public Flux<RatingResponseDTO> getAll() {
        log.info("GET /api/ratings");
        return ratingService.getAll();
    }

    @PostMapping
    @Operation(summary = "Create rating")
    public Mono<ResponseEntity<RatingResponseDTO>> create(
            @Valid @RequestBody CreateRatingRequestDTO request) {
        log.info("POST /api/ratings");
        return ratingService.create(request)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }
}
