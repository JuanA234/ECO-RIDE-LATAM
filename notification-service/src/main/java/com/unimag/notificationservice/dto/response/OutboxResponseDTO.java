package com.unimag.notificationservice.dto.response;

import com.unimag.notificationservice.entity.enums.OutboxStatus;

public record OutboxResponseDTO(Long id,
                                String eventType,
                                String payload,
                                OutboxStatus status,
                                Integer retries) {
}
