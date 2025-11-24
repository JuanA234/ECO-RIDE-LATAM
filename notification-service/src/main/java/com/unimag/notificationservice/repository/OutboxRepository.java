package com.unimag.notificationservice.repository;

import com.unimag.notificationservice.entity.Outbox;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OutboxRepository extends ReactiveCrudRepository<Outbox, Long> {
}
