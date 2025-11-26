package com.unimag.notificationservice.dto.request;

import com.unimag.notificationservice.entity.enums.Channel;

public record UpdateTemplateRequestDTO(String code,
                                       Channel channel,
                                       String subject,
                                       String body) {
}
