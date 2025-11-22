package com.unimag.notificationservice.repository;

import com.unimag.notificationservice.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
}
