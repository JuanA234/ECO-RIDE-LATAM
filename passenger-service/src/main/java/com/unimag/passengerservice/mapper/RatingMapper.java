package com.unimag.passengerservice.mapper;

import com.unimag.passengerservice.dto.request.CreateRatingRequestDTO;
import com.unimag.passengerservice.dto.response.RatingResponseDTO;
import com.unimag.passengerservice.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    RatingResponseDTO toDto(Rating rating);

    @Mapping(target = "id", ignore = true)
    Rating toEntity(CreateRatingRequestDTO request);
}
