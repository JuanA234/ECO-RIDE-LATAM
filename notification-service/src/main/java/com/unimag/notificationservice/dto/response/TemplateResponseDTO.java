package com.unimag.notificationservice.dto.response;

import com.unimag.notificationservice.entity.Channel;

public record TemplateResponseDTO(Long id,
                                  String code,
                                  Channel channel,
                                  String subject,
                                  String body) {
}
