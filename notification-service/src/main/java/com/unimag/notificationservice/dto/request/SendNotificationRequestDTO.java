package com.unimag.notificationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record SendNotificationRequestDTO(
        @NotBlank(message = "Template code is required")
        String templateCode,

        @NotBlank(message = "Recipient is required")
        String to,

        @NotNull(message = "Parameters are required")
        Map<String, String> params
) {
}
