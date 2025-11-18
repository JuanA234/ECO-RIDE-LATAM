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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final PassengerRepository passengerRepository;
    private final RatingMapper ratingMapper;

    @Override
    public RatingResponseDTO getById(Long id) {
        return ratingRepository.findById(id).map(ratingMapper::toDto).orElseThrow(() -> new RatingNotFoundException("Rating not found with id: " + id));
    }

    @Override
    public List<RatingResponseDTO> getAll() {
        return ratingRepository.findAll().stream().map(ratingMapper::toDto).toList();
    }

    @Override
    @Transactional
    public RatingResponseDTO create(CreateRatingRequestDTO request) {
        Rating ratingCreate = ratingRepository.save(ratingMapper.toEntity(request));

        Passenger passenger = passengerRepository.findById(request.toId()).orElseThrow(() -> new PassengerNotFoundException("Passenger not found with id: " + request.toId()));

        Double ratingAvg = ratingRepository.findAverageScoreByPassengerId(passenger.getId());
        passenger.setRatingAvg(ratingAvg != null ? ratingAvg : 0.0);

        passengerRepository.save(passenger);

        return ratingMapper.toDto(ratingCreate);
    }
}
