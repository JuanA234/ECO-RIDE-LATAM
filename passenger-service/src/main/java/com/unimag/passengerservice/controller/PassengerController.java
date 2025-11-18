package com.unimag.passengerservice.controller;

import com.unimag.passengerservice.dto.request.CreatePassengerRequestDTO;
import com.unimag.passengerservice.dto.request.UpdatePassengerRequestDTO;
import com.unimag.passengerservice.dto.response.PassengerResponseDTO;
import com.unimag.passengerservice.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {
    PassengerService passengerService;

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(passengerService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<PassengerResponseDTO>> getAll() {
        return ResponseEntity.ok(passengerService.getAll());
    }

    @PostMapping
    public ResponseEntity<PassengerResponseDTO> create(@Valid @RequestBody CreatePassengerRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(passengerService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UpdatePassengerRequestDTO request) {
        return ResponseEntity.ok(passengerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
