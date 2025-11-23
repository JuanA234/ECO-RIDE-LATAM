package com.unimag.tripservice.entity;

import com.unimag.tripservice.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "reservations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    private Long id;
    private Trip trip;
    private Long passengerId;
    private ReservationStatus status;
    private LocalDateTime createdAt;
}
