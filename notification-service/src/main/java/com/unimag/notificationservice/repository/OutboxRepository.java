package com.unimag.notificationservice.repository;

import com.unimag.notificationservice.entity.Outbox;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OutboxRepository extends R2dbcRepository<Outbox, Long> {
}
