package com.unimag.tripservice.mapper;


import com.unimag.tripservice.dto.trip.CreateTrip;
import com.unimag.tripservice.dto.trip.ResponseTrip;
import com.unimag.tripservice.entity.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TripMapper {


    ResponseTrip toResponseTrip(Trip trip);

    Trip toTrip(CreateTrip createTrip);
}
