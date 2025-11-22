package com.unimag.notificationservice.service;

import com.unimag.notificationservice.dto.request.SendNotificationRequestDTO;
import com.unimag.notificationservice.dto.response.OutboxResponseDTO;

public interface NotificationService {
    OutboxResponseDTO send(SendNotificationRequestDTO sendNotificationRequestDTO);
}
