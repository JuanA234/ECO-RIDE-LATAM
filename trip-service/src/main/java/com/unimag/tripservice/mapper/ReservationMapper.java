package com.unimag.tripservice.mapper;

import com.unimag.tripservice.dto.reservation.CreateReservation;
import com.unimag.tripservice.dto.reservation.ResponseReservation;
import com.unimag.tripservice.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ReservationMapper {


    ResponseReservation toResponseReservation(Reservation reservation);

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    Reservation toReservation(CreateReservation createReservation);
}
