package com.unimag.tripservice.entity;

import com.unimag.tripservice.enums.TripStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trips")
@Builder
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_id")
    private Long driverId;

    private String origin;

    private String destination;

    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "seats_total")
    private Integer seatsTotal;
    @Column(name = "seats_available")
    private Integer seatsAvailable;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private TripStatus status;
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
}
