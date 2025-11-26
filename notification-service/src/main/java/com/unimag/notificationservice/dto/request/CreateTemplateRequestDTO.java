package com.unimag.notificationservice.dto.request;

import com.unimag.notificationservice.entity.enums.Channel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTemplateRequestDTO(@NotBlank(message = "Code is required")
                                       String code,

                                       @NotNull(message = "Channel is required")
                                       Channel channel,

                                       String subject,

                                       @NotBlank(message = "Body is required")
                                       String body) {
}
