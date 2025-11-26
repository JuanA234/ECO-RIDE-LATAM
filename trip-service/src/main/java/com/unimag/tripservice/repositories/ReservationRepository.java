package com.unimag.tripservice.repositories;

import com.unimag.tripservice.entity.Reservation;
import com.unimag.tripservice.enums.ReservationStatus;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ReservationRepository extends R2dbcRepository<Reservation, Long> {

    Flux<Reservation> findByTripId(Long tripId);

    Flux<Reservation> findByPassengerId(Long passengerId);

    Flux<Reservation> findByStatus(ReservationStatus status);

    Mono<Reservation> findByIdAndPassengerId(Long id, Long passengerId);
}
