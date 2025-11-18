package com.unimag.passengerservice.controller;

import com.unimag.passengerservice.dto.request.CreateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.request.UpdateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.response.DriverProfileResponseDTO;
import com.unimag.passengerservice.service.DriverProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverProfileController {
    DriverProfileService driverProfileService;

    @GetMapping("/{id}")
    public ResponseEntity<DriverProfileResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(driverProfileService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<DriverProfileResponseDTO>> getAll() {
        return ResponseEntity.ok(driverProfileService.getAll());
    }

    @PostMapping("/profile")
    public ResponseEntity<DriverProfileResponseDTO> create(@Valid @RequestBody CreateDriverProfileRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverProfileService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DriverProfileResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UpdateDriverProfileRequestDTO request) {
        return ResponseEntity.ok(driverProfileService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
