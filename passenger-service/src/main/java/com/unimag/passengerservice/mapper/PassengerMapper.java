package com.unimag.passengerservice.mapper;

import com.unimag.passengerservice.dto.request.CreatePassengerRequestDTO;
import com.unimag.passengerservice.dto.response.PassengerResponseDTO;
import com.unimag.passengerservice.entity.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "string")
public interface PassengerMapper {

    PassengerResponseDTO toDTO(Passenger passenger);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", constant = "0.0")
    @Mapping(target = "ratingAvg", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "driverProfile", ignore = true)
    Passenger toEntity(CreatePassengerRequestDTO request);


}
