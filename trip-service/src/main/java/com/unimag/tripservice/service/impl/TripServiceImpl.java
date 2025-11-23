package com.unimag.tripservice.service.impl;


import com.unimag.tripservice.dto.reservation.CreateReservation;
import com.unimag.tripservice.dto.reservation.ResponseReservation;
import com.unimag.tripservice.dto.trip.CreateTrip;
import com.unimag.tripservice.dto.trip.ResponseTrip;
import com.unimag.tripservice.dto.trip.TripSearch;
import com.unimag.tripservice.entity.Trip;
import com.unimag.tripservice.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    @Override
    public Mono<ResponseTrip> createTrip(CreateTrip trip) {
        return null;
    }

    @Override
    public Mono<ResponseTrip> getTripById(Long id) {
        return null;
    }

    @Override
    public Flux<ResponseTrip> getTrips() {
        return null;
    }

    @Override
    public Flux<ResponseTrip> searchTrips(TripSearch request) {
        return null;
    }

    @Override
    @Transactional
    public Mono<ResponseReservation> createReservation(CreateReservation reservation) {
        return null;
    }

    @Override
    public Flux<ResponseReservation> getPassengerReservations(Long passengerId) {
        return null;
    }

    @Override
    public Mono<ResponseReservation> getReservationById(Long id) {
        return null;
    }

    @Override
    @Transactional
    public Mono<ResponseReservation> confirmReservation(Long id) {
        return null;
    }

    @Override
    @Transactional
    public Mono<ResponseReservation> cancelReservation(Long id, String reason) {
        return null;
    }

    @Override
    @Transactional
    public Mono<ResponseReservation> markReservationPaymentFailed(Long id) {
        return null;
    }
}
