package com.unimag.tripservice.entities;

import com.unimag.tripservice.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;
    @Column(name = "passenger_id")
    private String passengerId;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
