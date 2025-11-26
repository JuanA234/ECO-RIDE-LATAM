package com.unimag.notificationservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.notificationservice.dto.request.SendNotificationRequestDTO;
import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import com.unimag.notificationservice.entity.Outbox;
import com.unimag.notificationservice.entity.Template;
import com.unimag.notificationservice.entity.enums.Channel;
import com.unimag.notificationservice.entity.enums.OutboxStatus;
import com.unimag.notificationservice.exception.NotificationException;
import com.unimag.notificationservice.exception.notfound.TemplateNotFoundException;
import com.unimag.notificationservice.mapper.OutboxMapper;
import com.unimag.notificationservice.repository.OutboxRepository;
import com.unimag.notificationservice.repository.TemplateRepository;
import com.unimag.notificationservice.service.channels.EmailService;
import com.unimag.notificationservice.service.channels.PushService;
import com.unimag.notificationservice.service.channels.SmsService;
import com.unimag.notificationservice.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private SmsService smsService;

    @Mock
    private PushService pushService;

    @Mock
    private OutboxMapper outboxMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Template template;
    private Outbox outbox;
    private SendNotificationRequestDTO request;

    @BeforeEach
    void setUp() {
        template = Template.builder()
                .id(1L)
                .code("reservation-confirmed")
                .channel(Channel.EMAIL)
                .subject("Reservation Confirmed - Trip #{{tripId}}")
                .body("Hi {{passengerName}}, your reservation #{{tripId}} is confirmed.")
                .build();

        outbox = Outbox.builder()
                .id(1L)
                .eventType("reservation-confirmed")
                .payload("{}")
                .status(OutboxStatus.SENT)
                .retries(0)
                .build();

        request = new SendNotificationRequestDTO(
                "reservation-confirmed",
                "juan@example.com",
                Map.of(
                        "passengerName", "Juan Pérez",
                        "tripId", "123"
                )
        );
    }

    @Test
    @DisplayName("Should send notification successfully")
    void shouldSendNotificationSuccessfully() throws Exception {
        // Given
        when(templateRepository.findByCode("reservation-confirmed")).thenReturn(Mono.just(template));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(outbox));
        when(outboxMapper.toDTO(any(Outbox.class))).thenReturn(new OutboxResponseDTO(
                1L, "reservation-confirmed", "{}", OutboxStatus.SENT, 0));
        doNothing().when(emailService).send(anyString(), anyString(), anyString());

        // When
        Mono<OutboxResponseDTO> result = notificationService.send(request);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.status() == OutboxStatus.SENT &&
                                response.eventType().equals("reservation-confirmed")
                )
                .verifyComplete();

        verify(templateRepository).findByCode("reservation-confirmed");
        verify(emailService).send(
                eq("juan@example.com"),
                contains("Trip #123"),
                contains("Juan Pérez")
        );
        verify(outboxRepository, times(2)).save(any(Outbox.class));
    }

    @Test
    @DisplayName("Should throw exception when template not found")
    void shouldThrowExceptionWhenTemplateNotFound() {
        // Given
        when(templateRepository.findByCode("invalid-code")).thenReturn(Mono.empty());

        SendNotificationRequestDTO invalidRequest = new SendNotificationRequestDTO(
                "invalid-code",
                "juan@example.com",
                Map.of()
        );

        // When
        Mono<OutboxResponseDTO> result = notificationService.send(invalidRequest);

        // Then
        StepVerifier.create(result)
                .expectError(TemplateNotFoundException.class)
                .verify();

        verify(templateRepository).findByCode("invalid-code");
        verify(emailService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should mark as failed when email sending fails")
    void shouldMarkAsFailedWhenEmailSendingFails() throws Exception {
        // Given
        when(templateRepository.findByCode("reservation-confirmed")).thenReturn(Mono.just(template));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Outbox pendingOutbox = Outbox.builder()
                .id(1L)
                .eventType("reservation-confirmed")
                .status(OutboxStatus.PENDING)
                .retries(0)
                .build();

        Outbox failedOutbox = Outbox.builder()
                .id(1L)
                .eventType("reservation-confirmed")
                .status(OutboxStatus.FAILED)
                .retries(1)
                .build();

        when(outboxRepository.save(any(Outbox.class)))
                .thenReturn(Mono.just(pendingOutbox))
                .thenReturn(Mono.just(failedOutbox));

        when(outboxMapper.toDTO(any(Outbox.class))).thenReturn(new OutboxResponseDTO(
                1L, "reservation-confirmed", "{}", OutboxStatus.FAILED, 1));

        doThrow(new NotificationException("SMTP error"))
                .when(emailService).send(anyString(), anyString(), anyString());

        // When
        Mono<OutboxResponseDTO> result = notificationService.send(request);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.status() == OutboxStatus.FAILED &&
                                response.retries() == 1
                )
                .verifyComplete();

        verify(outboxRepository, times(2)).save(any(Outbox.class));
    }

    @Test
    @DisplayName("Should send SMS when channel is SMS")
    void shouldSendSmsWhenChannelIsSms() throws Exception {
        // Given
        Template smsTemplate = Template.builder()
                .id(2L)
                .code("reservation-confirmed-sms")
                .channel(Channel.SMS)
                .body("Your reservation #{{tripId}} is confirmed")
                .build();

        when(templateRepository.findByCode("reservation-confirmed-sms")).thenReturn(Mono.just(smsTemplate));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(outbox));
        when(outboxMapper.toDTO(any(Outbox.class))).thenReturn(new OutboxResponseDTO(
                1L, "reservation-confirmed-sms", "{}", OutboxStatus.SENT, 0));
        doNothing().when(smsService).send(anyString(), anyString());

        SendNotificationRequestDTO smsRequest = new SendNotificationRequestDTO(
                "reservation-confirmed-sms",
                "+573001234567",
                Map.of("tripId", "123")
        );

        // When
        Mono<OutboxResponseDTO> result = notificationService.send(smsRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.status() == OutboxStatus.SENT)
                .verifyComplete();

        verify(smsService).send(eq("+573001234567"), contains("123"));
        verify(emailService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should replace placeholders correctly")
    void shouldReplacePlaceholdersCorrectly() throws Exception {
        // Given
        when(templateRepository.findByCode("reservation-confirmed")).thenReturn(Mono.just(template));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(outbox));
        when(outboxMapper.toDTO(any(Outbox.class))).thenReturn(new OutboxResponseDTO(
                1L, "reservation-confirmed", "{}", OutboxStatus.SENT, 0));
        doNothing().when(emailService).send(anyString(), anyString(), anyString());

        // When
        Mono<OutboxResponseDTO> result = notificationService.send(request);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.status() == OutboxStatus.SENT)
                .verifyComplete();

        verify(emailService).send(
                eq("juan@example.com"),
                eq("Reservation Confirmed - Trip #123"),
                eq("Hi Juan Pérez, your reservation #123 is confirmed.")
        );
    }
}