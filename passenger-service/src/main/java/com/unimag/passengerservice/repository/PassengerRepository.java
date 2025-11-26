package com.unimag.passengerservice.repository;

import com.unimag.passengerservice.entity.Passenger;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PassengerRepository extends R2dbcRepository<Passenger, Long> {
    Mono<Boolean> existsByEmail(String email);
}
