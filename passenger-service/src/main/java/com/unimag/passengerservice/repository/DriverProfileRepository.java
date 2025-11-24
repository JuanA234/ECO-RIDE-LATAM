package com.unimag.passengerservice.repository;

import com.unimag.passengerservice.entity.DriverProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface DriverProfileRepository extends ReactiveCrudRepository<DriverProfile, Long> {
    Mono<Boolean> existsByPassengerId(Long passengerId);
}
