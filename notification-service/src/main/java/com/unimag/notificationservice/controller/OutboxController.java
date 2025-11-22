package com.unimag.notificationservice.controller;

import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import com.unimag.notificationservice.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outbox")
@RequiredArgsConstructor
public class OutboxController {

    private final OutboxService outboxService;

    @GetMapping("/{id}")
    public ResponseEntity<OutboxResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(outboxService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<OutboxResponseDTO>> getAll() {
        return ResponseEntity.ok(outboxService.getAll());
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<OutboxResponseDTO> retry(@PathVariable Long id) {
        return ResponseEntity.ok(outboxService.retry(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        outboxService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
