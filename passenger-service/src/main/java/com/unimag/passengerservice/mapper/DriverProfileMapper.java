package com.unimag.passengerservice.mapper;

import com.unimag.passengerservice.dto.request.CreateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.response.DriverProfileResponseDTO;
import com.unimag.passengerservice.entity.DriverProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverProfileMapper {

    @Mapping(target = "passengerId", source = "passenger.id")
    DriverProfileResponseDTO toDTO(DriverProfile driverProfile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passenger", ignore = true)
    @Mapping(target = "verificationStatus", ignore = true)
    DriverProfile toEntity(CreateDriverProfileRequestDTO request);

}
