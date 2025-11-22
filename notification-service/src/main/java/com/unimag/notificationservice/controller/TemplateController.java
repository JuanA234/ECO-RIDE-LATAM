package com.unimag.notificationservice.controller;

import com.unimag.notificationservice.dto.request.CreateTemplateRequestDTO;
import com.unimag.notificationservice.dto.request.UpdateTemplateRequestDTO;
import com.unimag.notificationservice.dto.response.TemplateResponseDTO;
import com.unimag.notificationservice.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TemplateResponseDTO>> getAll() {
        return ResponseEntity.ok(templateService.getAll());
    }

    @PostMapping
    public ResponseEntity<TemplateResponseDTO> create(@Valid @RequestBody CreateTemplateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TemplateResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UpdateTemplateRequestDTO request) {
        return ResponseEntity.ok(templateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
