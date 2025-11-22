package com.unimag.notificationservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.notificationservice.dto.request.SendNotificationRequestDTO;
import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import com.unimag.notificationservice.entity.Channel;
import com.unimag.notificationservice.entity.Outbox;
import com.unimag.notificationservice.entity.OutboxStatus;
import com.unimag.notificationservice.entity.Template;
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
    public OutboxResponseDTO send(SendNotificationRequestDTO request) {
        log.info("Sending notification using template: {} to: {}", request.templateCode(), request.to());

        //Buscar plantilla
        Template template = templateRepository.findByCode(request.templateCode())
                .orElseThrow(() -> new TemplateNotFoundException(
                        "Template not found with code: " + request.templateCode()));

        //Reemplazar placeholders
        String body = replacePlaceholders(template.getBody(), request.params());
        String subject = template.getSubject() != null
                ? replacePlaceholders(template.getSubject(), request.params())
                : null;

        //Crear registro en Outbox
        Outbox outbox = createOutboxEntry(request, template.getCode());
        outbox = outboxRepository.save(outbox);

        //Enviar según el canal
        try {
            sendByChannel(template.getChannel(), request.to(), subject, body);

            outbox.setStatus(OutboxStatus.SENT);

            log.info("Notification sent successfully to: {}", request.to());

        } catch (Exception e) {
            log.error("Failed to send notification to: {}", request.to(), e);

            outbox.setStatus(OutboxStatus.FAILED);
            outbox.setRetries(outbox.getRetries() + 1);
        }

        //Guardar resultado
        outbox = outboxRepository.save(outbox);
        return outboxMapper.toDTO(outbox);
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
