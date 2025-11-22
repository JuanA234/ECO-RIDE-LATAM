package com.unimag.tripservice.entity;

import com.unimag.tripservice.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reservations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;
    @Column(name = "passenger_id")
    private Long passengerId;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
