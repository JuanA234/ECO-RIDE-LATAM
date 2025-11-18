package com.unimag.passengerservice.service;

import com.unimag.passengerservice.dto.request.CreateRatingRequestDTO;
import com.unimag.passengerservice.dto.response.RatingResponseDTO;

import java.util.List;

public interface RatingService {
    RatingResponseDTO getById(Long id);
    List<RatingResponseDTO> getAll();
    RatingResponseDTO create(CreateRatingRequestDTO request);
}
