package com.unimag.tripservice.repositories;

import com.unimag.tripservice.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByTripId(Long tripId);
}
