package com.unimag.tripservice.services.Impl;

import com.unimag.tripservice.entities.Reservation;
import com.unimag.tripservice.entities.Trip;
import com.unimag.tripservice.enums.ReservationStatus;
import com.unimag.tripservice.repositories.ReservationRepository;
import com.unimag.tripservice.repositories.TripRepository;
import com.unimag.tripservice.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final TripRepository tripRepository;

    @Override
    public Reservation createReservation(Long tripId, String passengerId) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (trip.getSeatsAvailable() <= 0) {
            throw new RuntimeException("No seats available");
        }

        Reservation reservation = new Reservation();
        reservation.setTrip(trip);
        reservation.setPassengerId(passengerId);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setCreatedAt(LocalDateTime.now());

        // Reducir asiento disponible
        trip.setSeatsAvailable(trip.getSeatsAvailable() - 1);
        tripRepository.save(trip);

        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> getReservationsByTrip(Long tripId) {
        return reservationRepository.findByTripId((tripId));
    }

    @Override
    public Reservation cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);

        Trip trip = reservation.getTrip();
        trip.setSeatsAvailable(trip.getSeatsAvailable() + 1);
        tripRepository.save(trip);

        return reservationRepository.save(reservation);
    }
}

