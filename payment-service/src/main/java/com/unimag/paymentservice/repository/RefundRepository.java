package com.unimag.paymentservice.repository;

import com.unimag.paymentservice.entity.Refund;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface RefundRepository extends R2dbcRepository<Refund, Long> {
    Flux<Refund> findByChargeId(Long chargeId);
}
