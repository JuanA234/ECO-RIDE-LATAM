package com.unimag.notificationservice.service;

import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import com.unimag.notificationservice.entity.Outbox;
import com.unimag.notificationservice.entity.enums.OutboxStatus;
import com.unimag.notificationservice.exception.NotificationException;
import com.unimag.notificationservice.exception.notfound.OutboxNotFoundException;
import com.unimag.notificationservice.mapper.OutboxMapper;
import com.unimag.notificationservice.repository.OutboxRepository;
import com.unimag.notificationservice.service.impl.OutboxServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OutboxMapper outboxMapper;

    @InjectMocks
    private OutboxServiceImpl outboxService;

    private Outbox outbox;
    private OutboxResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        outbox = new Outbox();
        outbox.setId(1L);
        outbox.setStatus(OutboxStatus.PENDING);
        outbox.setRetries(0);

        responseDTO = new OutboxResponseDTO(
                1L,
                "reservation-confirmed",
                ("{}"),
                OutboxStatus.PENDING,
                0
        );
    }

    @Test
    void getById_ShouldReturnOutbox_WhenExists() {
        when(outboxRepository.findById(1L)).thenReturn(Mono.just(outbox));
        when(outboxMapper.toDTO(outbox)).thenReturn(responseDTO);

        StepVerifier.create(outboxService.getById(1L))
                .expectNext(responseDTO)
                .verifyComplete();

        verify(outboxRepository).findById(1L);
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        when(outboxRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(outboxService.getById(1L))
                .expectErrorMatches(ex ->
                        ex instanceof OutboxNotFoundException &&
                                ex.getMessage().contains("outbox entry not found with id")
                )
                .verify();
    }

    @Test
    void getAll_ShouldReturnListOfOutboxes() {
        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(outboxMapper.toDTO(outbox)).thenReturn(responseDTO);

        StepVerifier.create(outboxService.getAll())
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void retry_ShouldUpdateStatusAndRetries_WhenPending() {
        when(outboxRepository.findById(1L)).thenReturn(Mono.just(outbox));

        Outbox updated = new Outbox();
        updated.setId(1L);
        updated.setStatus(OutboxStatus.PENDING);
        updated.setRetries(1);

        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(updated));
        when(outboxMapper.toDTO(updated)).thenReturn(responseDTO);

        StepVerifier.create(outboxService.retry(1L))
                .expectNext(responseDTO)
                .verifyComplete();

        verify(outboxRepository).findById(1L);
        verify(outboxRepository).save(any(Outbox.class));
    }

    @Test
    void retry_ShouldThrowException_WhenNotFound() {
        when(outboxRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(outboxService.retry(1L))
                .expectError(OutboxNotFoundException.class)
                .verify();
    }

    @Test
    void retry_ShouldThrowException_WhenStatusIsSent() {
        outbox.setStatus(OutboxStatus.SENT);
        when(outboxRepository.findById(1L)).thenReturn(Mono.just(outbox));

        StepVerifier.create(outboxService.retry(1L))
                .expectErrorMatches(ex ->
                        ex instanceof NotificationException &&
                                ex.getMessage().contains("already sent")
                )
                .verify();

        verify(outboxRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDelete_WhenExists() {
        when(outboxRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(outboxRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(outboxService.delete(1L))
                .verifyComplete();

        verify(outboxRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        when(outboxRepository.existsById(1L)).thenReturn(Mono.just(false));

        StepVerifier.create(outboxService.delete(1L))
                .expectError(OutboxNotFoundException.class)
                .verify();
    }
}