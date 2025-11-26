package com.unimag.notificationservice.service.impl;

import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import com.unimag.notificationservice.entity.enums.OutboxStatus;
import com.unimag.notificationservice.exception.NotificationException;
import com.unimag.notificationservice.exception.notfound.OutboxNotFoundException;
import com.unimag.notificationservice.mapper.OutboxMapper;
import com.unimag.notificationservice.repository.OutboxRepository;
import com.unimag.notificationservice.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepository outboxRepository;
    private final OutboxMapper outboxMapper;

    @Override
    public Mono<OutboxResponseDTO> getById(Long id) {
        log.debug("Retrieving outbox entry with id: {}", id);
        return outboxRepository.findById(id)
                .switchIfEmpty(Mono.error(new OutboxNotFoundException("outbox entry not found with id: " + id)))
                .map(outboxMapper::toDTO);
    }

    @Override
    public Flux<OutboxResponseDTO> getAll() {
        log.debug("Retrieving all outboxes entries");
        return outboxRepository.findAll()
                .map(outboxMapper::toDTO);
    }

    @Override
    @Transactional
    public Mono<OutboxResponseDTO> retry(Long id) {
        log.info("Retrying Outbox entry with id: {}", id);

        return outboxRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new OutboxNotFoundException("Outbox entry not found with id: " + id)
                ))
                .flatMap(outbox -> {
                    if (outbox.getStatus() == OutboxStatus.SENT) {
                        return Mono.error(
                                new NotificationException("Cannot retry a notification that was already sent")
                        );
                    }

                    outbox.setStatus(OutboxStatus.PENDING);
                    outbox.setRetries(outbox.getRetries() + 1);

                    return outboxRepository.save(outbox);
                })
                .doOnSuccess(updated -> log.info("Outbox entry marked for retry"))
                .map(outboxMapper::toDTO);
    }

    @Override
    @Transactional
    public Mono<Void> delete(Long id) {
        log.info("Deleting outbox entry with id: {}", id);

        return outboxRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(
                                new OutboxNotFoundException("Outbox entry not found with id: " + id)
                        );
                    }
                    return outboxRepository.deleteById(id);
                });
    }
}
