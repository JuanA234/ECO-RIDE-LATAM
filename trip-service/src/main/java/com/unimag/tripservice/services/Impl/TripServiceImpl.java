package com.unimag.tripservice.services.Impl;

import com.unimag.tripservice.entities.Trip;
import com.unimag.tripservice.enums.TripStatus;
import com.unimag.tripservice.repositories.TripRepository;
import com.unimag.tripservice.services.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    @Override
    public Trip createTrip(Trip trip) {
        trip.setStatus(TripStatus.SCHEDULED);
        trip.setSeatsAvailable(trip.getSeatsTotal());
        trip.setStartTime(LocalDateTime.now());
        return tripRepository.save(trip);
    }

    @Override
    public Trip updateTrip(Long id, Trip updatedTrip) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        trip.setOrigin(updatedTrip.getOrigin());
        trip.setDestination(updatedTrip.getDestination());
        trip.setSeatsTotal(updatedTrip.getSeatsTotal());
        trip.setSeatsAvailable(updatedTrip.getSeatsAvailable());
        trip.setPrice(updatedTrip.getPrice());
        trip.setStatus(updatedTrip.getStatus());

        return tripRepository.save(trip);
    }

    @Override
    public Trip getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
    }

    @Override
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @Override
    public void deleteTrip(Long id) {
        tripRepository.deleteById(id);
    }
}
