package com.unimag.passengerservice.service.impl;

import com.unimag.passengerservice.dto.request.CreateRatingRequestDTO;
import com.unimag.passengerservice.dto.response.RatingResponseDTO;
import com.unimag.passengerservice.entity.Passenger;
import com.unimag.passengerservice.entity.Rating;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final PassengerRepository passengerRepository;
    private final RatingMapper ratingMapper;

    @Override
    public RatingResponseDTO getById(Long id) {
        log.debug("Get rating by id: {}", id);

        return ratingRepository.findById(id)
                .map(ratingMapper::toDto)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found with id: " + id));
    }

    @Override
    public List<RatingResponseDTO> getAll() {
        log.debug("Get all ratings");

        return ratingRepository.findAll()
                .stream()
                .map(ratingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public RatingResponseDTO create(CreateRatingRequestDTO request) {
        log.info("Creating rating from passenger {} to passenger {} for trip {}",
                request.fromId(), request.toId(), request.tripId());

        if (request.fromId().equals(request.toId())) {
            log.warn("Passenger {} attempted to rate themselves", request.fromId());
            throw new IllegalArgumentException("Cannot rate yourself");
        }

        if (!passengerRepository.existsById(request.fromId())) {
            log.error("From passenger not found with ID: {}", request.fromId());
            throw new PassengerNotFoundException("From passenger not found");
        }

        if (!passengerRepository.existsById(request.toId())) {
            log.error("To passenger not found with ID: {}", request.toId());
            throw new PassengerNotFoundException("To passenger not found");
        }

        if (ratingRepository.existsByTripIdAndFromIdAndToId(
                request.tripId(), request.fromId(), request.toId())) {
            log.warn("Rating already exists for trip {} from {} to {}",
                    request.tripId(), request.fromId(), request.toId());
            throw new IllegalArgumentException("Rating already exists for this trip");
        }

        Rating ratingCreate = ratingRepository.save(ratingMapper.toEntity(request));

        Passenger passenger = passengerRepository.findById(request.toId())
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with id: " + request.toId()));

        Double ratingAvg = ratingRepository.findAverageScoreByPassengerId(passenger.getId());
        passenger.setRatingAvg(ratingAvg != null ? ratingAvg : 0.0);

        passengerRepository.save(passenger);

        return ratingMapper.toDto(ratingCreate);
    }
}
