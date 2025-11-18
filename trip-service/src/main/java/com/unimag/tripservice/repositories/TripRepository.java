package com.unimag.tripservice.repositories;

import com.unimag.tripservice.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
