package com.unimag.passengerservice.repository;

import com.unimag.passengerservice.entity.Passenger;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PassengerRepository extends ReactiveCrudRepository<Passenger, Long> {
    Mono<Boolean> existsByEmail(String email);
}
