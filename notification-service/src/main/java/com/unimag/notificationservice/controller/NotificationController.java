package com.unimag.notificationservice.controller;

import com.unimag.notificationservice.dto.request.SendNotificationRequestDTO;
import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import com.unimag.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotificationController {

    NotificationService notificationService;

    @PostMapping
    public ResponseEntity<OutboxResponseDTO> send(@RequestBody SendNotificationRequestDTO sendNotificationRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.send(sendNotificationRequestDTO));
    }

}
