package com.unimag.passengerservice.service;

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
import com.unimag.passengerservice.service.impl.RatingServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private RatingMapper ratingMapper;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private Rating rating;
    private CreateRatingRequestDTO request;
    private RatingResponseDTO ratingResponseDTO;

    @BeforeEach
    void setUp() {
        rating = new Rating();
        rating.setId(1L);
        rating.setScore(5);

        request = new CreateRatingRequestDTO(
                10L,
                2L,
                3L,
                5,
                ""
        );

        ratingResponseDTO = new RatingResponseDTO(
                1L, 10L, 2L, 3L, 5, ""
        );
    }

    @Test
    void getById_success() {
        when(ratingRepository.findById(1L)).thenReturn(Mono.just(rating));
        when(ratingMapper.toDto(rating)).thenReturn(ratingResponseDTO);

        StepVerifier.create(ratingService.getById(1L))
                .expectNext(ratingResponseDTO)
                .verifyComplete();
    }

    @Test
    void getById_notFound() {
        when(ratingRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(ratingService.getById(1L))
                .expectError(RatingNotFoundException.class)
                .verify();
    }

    @Test
    void getAll_success() {
        when(ratingRepository.findAll()).thenReturn(Flux.just(rating));
        when(ratingMapper.toDto(rating)).thenReturn(ratingResponseDTO);

        StepVerifier.create(ratingService.getAll())
                .expectNext(ratingResponseDTO)
                .verifyComplete();
    }

    @Test
    void create_success() {
        when(passengerRepository.existsById(anyLong())).thenReturn(Mono.just(true));
        when(ratingRepository.existsByTripIdAndFromIdAndToId(anyLong(), anyLong(), anyLong()))
                .thenReturn(Mono.just(false));

        Rating savedRating = new Rating();
        savedRating.setId(1L);
        savedRating.setScore(5);

        when(ratingMapper.toEntity(any())).thenReturn(savedRating);
        when(ratingRepository.save(any())).thenReturn(Mono.just(savedRating));

        Passenger passenger = new Passenger();
        passenger.setId(3L);
        passenger.setRatingAvg(0.0);

        when(passengerRepository.findById(3L)).thenReturn(Mono.just(passenger));
        when(ratingRepository.findAverageScoreByPassengerId(3L)).thenReturn(Mono.just(5.0));
        when(passengerRepository.save(any())).thenReturn(Mono.just(passenger));

        when(ratingMapper.toDto(savedRating)).thenReturn(ratingResponseDTO);

        StepVerifier.create(ratingService.create(request))
                .expectNext(ratingResponseDTO)
                .verifyComplete();
    }

    @Test
    void create_cannotRateYourself() {
        CreateRatingRequestDTO bad =
                new CreateRatingRequestDTO(10L, 5L, 5L, 4, "test");

        StepVerifier.create(ratingService.create(bad))
                .expectErrorMatches(ex ->
                        ex instanceof IllegalArgumentException &&
                                ex.getMessage().equals("Cannot rate yourself")
                )
                .verify();
    }

    @Test
    void create_fromPassengerNotFound() {
        when(passengerRepository.existsById(2L)).thenReturn(Mono.just(false));

        StepVerifier.create(ratingService.create(request))
                .expectError(PassengerNotFoundException.class)
                .verify();
    }

    @Test
    void create_toPassengerNotFound() {
        when(passengerRepository.existsById(2L)).thenReturn(Mono.just(true));
        when(passengerRepository.existsById(3L)).thenReturn(Mono.just(false));

        StepVerifier.create(ratingService.create(request))
                .expectError(PassengerNotFoundException.class)
                .verify();
    }

    @Test
    void create_ratingAlreadyExists() {
        when(passengerRepository.existsById(2L)).thenReturn(Mono.just(true));
        when(passengerRepository.existsById(3L)).thenReturn(Mono.just(true));
        when(ratingRepository.existsByTripIdAndFromIdAndToId(10L, 2L, 3L))
                .thenReturn(Mono.just(true));

        StepVerifier.create(ratingService.create(request))
                .expectError(TripWithRatingAlreadyExistsException.class)
                .verify();
    }
}