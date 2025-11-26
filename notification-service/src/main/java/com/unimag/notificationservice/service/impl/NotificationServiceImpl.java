package com.unimag.notificationservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.notificationservice.dto.request.SendNotificationRequestDTO;
import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import com.unimag.notificationservice.entity.Outbox;
import com.unimag.notificationservice.entity.enums.Channel;
import com.unimag.notificationservice.entity.enums.OutboxStatus;
import com.unimag.notificationservice.exception.NotificationException;
import com.unimag.notificationservice.exception.notfound.TemplateNotFoundException;
import com.unimag.notificationservice.mapper.OutboxMapper;
import com.unimag.notificationservice.repository.OutboxRepository;
import com.unimag.notificationservice.repository.TemplateRepository;
import com.unimag.notificationservice.service.NotificationService;
import com.unimag.notificationservice.service.channels.EmailService;
import com.unimag.notificationservice.service.channels.PushService;
import com.unimag.notificationservice.service.channels.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final TemplateRepository templateRepository;
    private final OutboxRepository outboxRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final PushService pushService;
    private final OutboxMapper outboxMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Mono<OutboxResponseDTO> send(SendNotificationRequestDTO request) {

        log.info("Sending notification using template {} to {}", request.templateCode(), request.to());

        return templateRepository.findByCode(request.templateCode())
                .switchIfEmpty(Mono.error(new TemplateNotFoundException("Template not found with code: " + request.templateCode())))
                .flatMap(template -> {

                    String body = replacePlaceholders(template.getBody(), request.params());
                    String subject = template.getSubject() != null
                            ? replacePlaceholders(template.getSubject(), request.params())
                            : null;

                    Outbox outbox = createOutboxEntry(request, template.getCode());

                    return outboxRepository.save(outbox)
                            .flatMap(savedOutbox ->
                                    Mono.fromRunnable(() ->
                                                    sendByChannel(template.getChannel(), request.to(), subject, body)
                                            )
                                            .thenReturn(savedOutbox)
                                            .flatMap(ob -> {
                                                ob.setStatus(OutboxStatus.SENT);
                                                return outboxRepository.save(ob);
                                            })
                                            .onErrorResume(e -> {
                                                log.error("Error sending notification to {}: {}", request.to(), e.getMessage());
                                                savedOutbox.setStatus(OutboxStatus.FAILED);
                                                return outboxRepository.save(savedOutbox);
                                            })
                            );
                })
                .map(outboxMapper::toDTO);
    }

    private void sendByChannel(Channel channel, String to, String subject, String body) {
        switch (channel) {
            case EMAIL -> emailService.send(to, subject, body);
            case SMS -> smsService.send(to, body);
            case PUSH -> pushService.send(to, subject, body);
            default -> throw new NotificationException("Unknown channel: " + channel);
        }
    }

    private String replacePlaceholders(String template, Map<String, String> params) {
        String result = template;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            result = result.replace(placeholder, value);
        }

        return result;
    }

    private Outbox createOutboxEntry(SendNotificationRequestDTO request, String templateCode) {
        try {
            return Outbox.builder()
                    .eventType(templateCode)
                    .payload(objectMapper.writeValueAsString(request))
                    .status(OutboxStatus.PENDING)
                    .retries(0)
                    .build();

        } catch (JsonProcessingException e) {
            throw new NotificationException("Failed to serialize notification request: " + e.getMessage());
        }
    }
}
