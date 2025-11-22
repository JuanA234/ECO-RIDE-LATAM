package com.unimag.notificationservice.service.impl;

import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import com.unimag.notificationservice.entity.Outbox;
import com.unimag.notificationservice.entity.OutboxStatus;
import com.unimag.notificationservice.exception.NotificationException;
import com.unimag.notificationservice.exception.notfound.OutboxNotFoundException;
import com.unimag.notificationservice.mapper.OutboxMapper;
import com.unimag.notificationservice.repository.OutboxRepository;
import com.unimag.notificationservice.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxServiceImpl implements OutboxService {

    OutboxRepository outboxRepository;
    OutboxMapper outboxMapper;

    @Override
    @Transactional(readOnly = true)
    public OutboxResponseDTO getById(Long id) {
        log.debug("Retrieving outbox entry with id: {}", id);
        return outboxRepository.findById(id)
                .map(outboxMapper::toDTO)
                .orElseThrow(() -> new OutboxNotFoundException("Notification not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutboxResponseDTO> getAll() {
        log.debug("Retrieving all outboxes entries");
        return outboxRepository.findAll()
                .stream()
                .map(outboxMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public OutboxResponseDTO retry(Long id) {
        log.info("Retrying Outbox entry with id: {}", id);

        Outbox outbox = outboxRepository.findById(id)
                .orElseThrow(() -> new OutboxNotFoundException("Outbox entry not found with id: " + id));

        if (outbox.getStatus() == OutboxStatus.SENT) {
            throw new NotificationException("Cannot retry a notification that was already sent");
        }

        outbox.setStatus(OutboxStatus.PENDING);
        outbox.setRetries(outbox.getRetries() + 1);

        Outbox updated = outboxRepository.save(outbox);

        log.info("Outbox entry marked for retry");

        return outboxMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting outbox entry with id: {}", id);
        if(!outboxRepository.existsById(id)) {
            throw new OutboxNotFoundException("Outbox entry not found with id: " + id);
        }

        outboxRepository.deleteById(id);
    }
}
