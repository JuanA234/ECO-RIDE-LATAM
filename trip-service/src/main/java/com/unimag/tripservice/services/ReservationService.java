package com.unimag.tripservice.services;

import com.unimag.tripservice.entities.Reservation;

import java.util.List;

public interface ReservationService {
    Reservation createReservation(Long tripId, String passengerId);
    List<Reservation> getReservationsByTrip(Long tripId);
    Reservation cancelReservation(Long id);
}
