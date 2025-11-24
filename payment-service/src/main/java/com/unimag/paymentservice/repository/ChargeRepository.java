package com.unimag.paymentservice.repository;

import com.unimag.paymentservice.entity.Charge;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ChargeRepository extends R2dbcRepository<Charge, Long> {
    Mono<Charge> findByPaymentIntentId(Long paymentIntentId);
}
