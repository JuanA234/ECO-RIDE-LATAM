package com.unimag.passengerservice.repository;

import com.unimag.passengerservice.entity.DriverProfile;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface DriverProfileRepository extends R2dbcRepository<DriverProfile, Long> {
    Mono<Boolean> existsByPassengerId(Long passengerId);
}
