package com.unimag.notificationservice.repository;

import com.unimag.notificationservice.entity.Template;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TemplateRepository extends R2dbcRepository<Template, Long> {
    Mono<Template> findByCode(String code);
}
