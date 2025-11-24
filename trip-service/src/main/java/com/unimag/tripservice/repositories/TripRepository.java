package com.unimag.tripservice.repositories;

import com.unimag.tripservice.entity.Trip;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends R2dbcRepository<Trip, Long> {
}
