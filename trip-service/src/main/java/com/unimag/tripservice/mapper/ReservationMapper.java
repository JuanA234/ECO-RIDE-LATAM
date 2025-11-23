package com.unimag.tripservice.mapper;

import com.unimag.tripservice.dto.reservation.CreateReservation;
import com.unimag.tripservice.dto.reservation.ResponseReservation;
import com.unimag.tripservice.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mappings({
            @Mapping(source = "trip.id", target = "tripId")
    })
    ResponseReservation toResponseReservation(Reservation reservation);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "tripId", target = "trip.id")
    })
    Reservation toReservation(CreateReservation createReservation);
}
