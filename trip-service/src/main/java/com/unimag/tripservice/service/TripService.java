package com.unimag.tripservice.service;

import com.unimag.tripservice.dto.reservation.CreateReservation;
import com.unimag.tripservice.dto.reservation.ResponseReservation;
import com.unimag.tripservice.dto.trip.CreateTrip;
import com.unimag.tripservice.dto.trip.ResponseTrip;
import com.unimag.tripservice.dto.trip.TripSearch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TripService {

    Mono<ResponseTrip> createTrip(CreateTrip trip);

    Mono<ResponseTrip> getTripById(Long id);

    Flux<ResponseTrip> getTrips();

    Flux<ResponseTrip> searchTrips(TripSearch request);

    Mono<ResponseReservation> createReservation(CreateReservation reservation);

    Flux<ResponseReservation> getPassengerReservations(Long passengerId);

    Mono<ResponseReservation> getReservationById(Long id);

    Mono<ResponseReservation> confirmReservation(Long id);

    Mono<ResponseReservation> cancelReservation(Long id, String reason);

    Mono<ResponseReservation> markReservationPaymentFailed(Long id);
}
