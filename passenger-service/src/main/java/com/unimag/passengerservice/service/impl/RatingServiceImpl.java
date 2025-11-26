package com.unimag.passengerservice.service.impl;

import com.unimag.passengerservice.dto.request.CreateRatingRequestDTO;
import com.unimag.passengerservice.dto.response.RatingResponseDTO;
import com.unimag.passengerservice.entity.Passenger;
import com.unimag.passengerservice.entity.Rating;
import com.unimag.passengerservice.exception.alreadyexists.TripWithRatingAlreadyExistsException;
import com.unimag.passengerservice.exception.notfound.PassengerNotFoundException;
import com.unimag.passengerservice.exception.notfound.RatingNotFoundException;
import com.unimag.passengerservice.mapper.RatingMapper;
import com.unimag.passengerservice.repository.PassengerRepository;
import com.unimag.passengerservice.repository.RatingRepository;
import com.unimag.passengerservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final PassengerRepository passengerRepository;
    private final RatingMapper ratingMapper;

    @Override
    public Mono<RatingResponseDTO> getById(Long id) {
        log.debug("Get rating by id: {}", id);

        return ratingRepository.findById(id)
                .switchIfEmpty(Mono.error(new RatingNotFoundException("Rating not found with id: " + id)))
                .map(ratingMapper::toDto);
    }

    @Override
    public Flux<RatingResponseDTO> getAll() {
        log.debug("Get all ratings");

        return ratingRepository.findAll()
                .map(ratingMapper::toDto);
    }

    @Override
    @Transactional
    public Mono<RatingResponseDTO> create(CreateRatingRequestDTO request) {
        log.info("Creating rating from passenger {} to passenger {} for trip {}",
                request.fromId(), request.toId(), request.tripId());

        if (request.fromId().equals(request.toId())) {
            return Mono.error(new IllegalArgumentException("Cannot rate yourself"));
        }

        return passengerRepository.existsById(request.fromId())
                .switchIfEmpty(Mono.error(new PassengerNotFoundException("From passenger not found (null response)")))
                .flatMap(existsFrom -> {
                    if (!existsFrom) {
                        return Mono.error(new PassengerNotFoundException("From passenger not found"));
                    }

                    return passengerRepository.existsById(request.toId())
                            .switchIfEmpty(Mono.error(new PassengerNotFoundException("To passenger not found (null response)")));
                })
                .flatMap(existsTo -> {
                    if (!existsTo) {
                        return Mono.error(new PassengerNotFoundException("To passenger not found"));
                    }

                    return ratingRepository.existsByTripIdAndFromIdAndToId(
                            request.tripId(),
                            request.fromId(),
                            request.toId()
                    ).switchIfEmpty(Mono.error(new RuntimeException("Repository returned null Mono")));
                })
                .flatMap(existsRating -> {
                    if (existsRating) {
                        return Mono.error(new TripWithRatingAlreadyExistsException("Rating already exists"));
                    }

                    Rating rating = ratingMapper.toEntity(request);

                    return ratingRepository.save(rating)
                            .switchIfEmpty(Mono.error(new RuntimeException("Save returned empty")));
                })

                .flatMap(savedRating ->
                        passengerRepository.findById(request.toId())
                                .switchIfEmpty(Mono.error(new PassengerNotFoundException("To passenger not found after save")))
                                .flatMap(passenger ->
                                        ratingRepository.findAverageScoreByPassengerId(passenger.getId())
                                                .defaultIfEmpty(0.0)
                                                .flatMap(avg -> {
                                                    passenger.setRatingAvg(avg);
                                                    return passengerRepository.save(passenger);
                                                })
                                                .thenReturn(savedRating)
                                )
                )
                .map(ratingMapper::toDto);
    }
}
