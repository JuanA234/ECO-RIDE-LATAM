package com.unimag.passengerservice.repository;

import com.unimag.passengerservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.toId = :id")
    Double findAverageScoreByPassengerId(Long id);
}
