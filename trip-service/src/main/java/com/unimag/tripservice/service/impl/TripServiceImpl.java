package com.unimag.tripservice.service.impl;


import com.unimag.tripservice.dto.event.ReservationRequested;
import com.unimag.tripservice.dto.reservation.CreateReservation;
import com.unimag.tripservice.dto.reservation.ResponseReservation;
import com.unimag.tripservice.dto.trip.CreateTrip;
import com.unimag.tripservice.dto.trip.ResponseTrip;
import com.unimag.tripservice.dto.trip.TripSearch;
import com.unimag.tripservice.entity.Reservation;
import com.unimag.tripservice.entity.Trip;
import com.unimag.tripservice.enums.ReservationStatus;
import com.unimag.tripservice.enums.TripStatus;
import com.unimag.tripservice.exception.notFound.ReservationNotFoundException;
import com.unimag.tripservice.exception.notFound.TripNotFoundException;
import com.unimag.tripservice.mapper.ReservationMapper;
import com.unimag.tripservice.mapper.TripMapper;
import com.unimag.tripservice.repositories.ReservationRepository;
import com.unimag.tripservice.repositories.TripRepository;
import com.unimag.tripservice.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final ReservationRepository reservationRepository;
    private final TripMapper tripMapper;
    private final ReservationMapper reservationMapper;
    private final StreamBridge streamBridge;


    @Override
    public Mono<ResponseTrip> createTrip(CreateTrip request) {

        Trip trip = tripMapper.toTrip(request);
        trip.setStatus(TripStatus.SCHEDULED);

        return tripRepository.save(trip)
                .doOnSuccess(t -> log.info("Trip created: {}", t.getId()))
                .map(tripMapper::toResponseTrip);
    }

    @Override
    public Mono<ResponseTrip> getTripById(Long id) {
        return tripRepository.findById(id)
                .map(tripMapper::toResponseTrip)
                .switchIfEmpty(Mono.error(new TripNotFoundException(("Trip not found"))));
    }

    @Override
    public Flux<ResponseTrip> getTrips() {
        return tripRepository.findAll()
                .map(tripMapper::toResponseTrip);
    }

    @Override
    public Flux<ResponseTrip> searchTrips(TripSearch request) {
        if(request.origin()!= null && request.destination()!= null) {
            LocalDateTime startTime = request.startTime() != null
                    ? request.startTime()
                    : LocalDateTime.now();

            return tripRepository.findAvailableTrips(
                    request.origin(),
                    request.destination(),
                    startTime
            ).map(tripMapper::toResponseTrip);

        }
        return tripRepository.findByStatus(TripStatus.SCHEDULED)
                .map(tripMapper::toResponseTrip);
    }

    @Override
    @Transactional
    public Mono<ResponseReservation> createReservation(CreateReservation request) {
        return tripRepository.findById(request.tripId())
                .switchIfEmpty(Mono.error(new TripNotFoundException(("Trip not found"))))
                .flatMap(trip -> {
                    if(trip.getSeatsAvailable() <= 0){
                        return Mono.error(new TripNotFoundException(("Trip not found")));
                    }

                    Reservation reservation = reservationMapper.toReservation(request);
                    reservation.setStatus(ReservationStatus.PENDING);
                    reservation.setCreatedAt(LocalDateTime.now());

                    return reservationRepository.save(reservation)
                            .flatMap(savedReservation ->
                                    tripRepository.decrementAvailableSeats(trip.getId())
                                            .thenReturn(savedReservation)
                            )
                            .flatMap(savedReservation -> {
                                ReservationRequested event = ReservationRequested.builder()
                                        .reservationId(savedReservation.getId())
                                        .tripId(savedReservation.getTripId())
                                        .passengerId(savedReservation.getPassengerId())
                                        .amount(trip.getPrice())
                                        .build();
                                streamBridge.send("reservationRequested-out-0", event);
                                log.info("ReservationRequested event published: {}", event);

                                return Mono.just(savedReservation);
                            });
                }).map(reservationMapper::toResponseReservation);
    }

    @Override
    public Flux<ResponseReservation> getPassengerReservations(Long passengerId) {
        return reservationRepository.findByPassengerId(passengerId)
                .map(reservationMapper::toResponseReservation);
    }

    @Override
    public Mono<ResponseReservation> getReservationById(Long id) {
        return reservationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException(("Reservation not found"))))
                .map(reservationMapper::toResponseReservation);
    }

    @Override
    @Transactional
    public Mono<ResponseReservation> confirmReservation(Long id) {
        return reservationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException("Reservation not found")))
                .flatMap(reservation -> {
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                    return reservationRepository.save(reservation);
                })
                .doOnSuccess(reservation -> log.info("Reservation confirmed: {}", reservation.getId()))
                .map(reservationMapper::toResponseReservation);

    }

    @Override
    @Transactional
    public Mono<ResponseReservation> cancelReservation(Long id, String reason) {
        return reservationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException("Reservation not found")))
                .flatMap(reservation -> {
                    reservation.setStatus(ReservationStatus.CANCELLED);

                    return reservationRepository.save(reservation)
                            .flatMap(savedReservation ->
                                tripRepository.incrementAvailableSeats(savedReservation.getTripId())
                                        .thenReturn(savedReservation)
                            );
                })
                .doOnSuccess(reservation -> log.info("Reservation cancelled: {}", reservation.getId()))
                .map(reservationMapper::toResponseReservation);
    }

    @Override
    @Transactional
    public Mono<ResponseReservation> markReservationPaymentFailed(Long id) {
        return reservationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException("Reservation not found")))
                .flatMap(reservation -> {
                    reservation.setStatus(ReservationStatus.PAYMENT_FAILED);

                    return reservationRepository.save(reservation)
                            .flatMap(savedReservation ->
                                    tripRepository.incrementAvailableSeats(savedReservation.getTripId())
                                            .thenReturn(savedReservation)
                            );

                })
                .doOnSuccess(reservation -> log.info("Reservation payment failed: {}", reservation.getId()))
                .map(reservationMapper::toResponseReservation)
        ;
    }
}
