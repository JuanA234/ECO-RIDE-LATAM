package com.unimag.tripservice.repositories;

import com.unimag.tripservice.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
