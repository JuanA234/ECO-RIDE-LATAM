package com.unimag.notificationservice.service;

import com.unimag.notificationservice.dto.request.SendNotificationRequestDTO;
import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import reactor.core.publisher.Mono;

public interface NotificationService {
    Mono<OutboxResponseDTO> send(SendNotificationRequestDTO request);
}
