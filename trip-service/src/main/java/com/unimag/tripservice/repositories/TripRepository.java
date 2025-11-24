package com.unimag.tripservice.repositories;

import com.unimag.tripservice.entity.Trip;
import com.unimag.tripservice.enums.TripStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface TripRepository extends R2dbcRepository<Trip, Long> {

    Flux<Trip> findByStatus(TripStatus status);

    Flux<Trip> findByDriverId(Long driverId);


    @Query("SELECT * FROM trips WHERE origin = :origin AND destination = :destination " +
            "AND start_time >= :startTime AND status = 'SCHEDULED' " +
            "AND seats_available > 0 ORDER BY start_time")
    Flux<Trip> findAvailableTrips(@Param("origin") String origin,
                                  @Param("destination") String destination,
                                  @Param("startTime") LocalDateTime startTime);

    @Query("UPDATE trips SET seats_available = seats_available - 1, updated_at = NOW() " +
            "WHERE id = :tripId AND seats_available > 0")
    Mono<Integer> decrementAvailableSeats(@Param("tripId") Long tripId);

    @Query("UPDATE trips SET seats_available = seats_available + 1, updated_at = NOW() " +
            "WHERE id = :tripId")
    Mono<Integer> incrementAvailableSeats(@Param("tripId") Long tripId);
}
