package com.unimag.tripservice.repositories;

import com.unimag.tripservice.entity.Reservation;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends R2dbcRepository<Reservation, Long> {
}
