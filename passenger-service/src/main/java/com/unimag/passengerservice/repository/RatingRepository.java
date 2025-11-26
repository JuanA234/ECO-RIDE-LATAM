package com.unimag.passengerservice.repository;

import com.unimag.passengerservice.entity.Rating;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RatingRepository extends ReactiveCrudRepository<Rating, Long> {
    @Query("SELECT AVG(r.score) FROM ratings r WHERE r.to_id = :id")
    Mono<Double> findAverageScoreByPassengerId(Long id);

    Mono<Boolean> existsByTripIdAndFromIdAndToId(Long tripId, Long fromId, Long toId);
}
