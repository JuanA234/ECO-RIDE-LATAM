package com.unimag.passengerservice.controller;

import com.unimag.passengerservice.dto.request.CreateRatingRequestDTO;
import com.unimag.passengerservice.dto.response.RatingResponseDTO;
import com.unimag.passengerservice.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    RatingService ratingService;

    @GetMapping("/{id}")
    public ResponseEntity<RatingResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<RatingResponseDTO>> getAll() {
        return ResponseEntity.ok(ratingService.getAll());
    }

    @PostMapping
    public ResponseEntity<RatingResponseDTO> create(@Valid @RequestBody CreateRatingRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.create(request));
    }
}
