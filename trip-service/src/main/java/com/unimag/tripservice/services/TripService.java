package com.unimag.tripservice.services;

import com.unimag.tripservice.entities.Trip;

import java.util.List;

public interface TripService {
    Trip createTrip(Trip trip);
    Trip updateTrip(Long id, Trip trip);
    Trip getTripById(Long id);
    List<Trip> getAllTrips();
    void deleteTrip(Long id);
}
