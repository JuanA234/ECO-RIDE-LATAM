package com.unimag.paymentservice.repository;

import com.unimag.paymentservice.entity.PaymentIntent;
import com.unimag.paymentservice.enums.PaymentStatus;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface PaymentIntentRepository extends R2dbcRepository<PaymentIntent, Long> {

    Mono<PaymentIntent> findByReservationId(Long reservationId);
    Flux<PaymentIntent> findByStatus(PaymentStatus status);
}
