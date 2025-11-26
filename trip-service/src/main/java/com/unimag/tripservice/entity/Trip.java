package com.unimag.tripservice.entity;

import com.unimag.tripservice.enums.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trips")
@Builder
public class Trip {
    @Id
    private Long id;

    private Long driverId;

    private String origin;

    private String destination;

    private LocalDateTime startTime;
    private Integer seatsTotal;
    private Integer seatsAvailable;
    private BigDecimal price;
    private TripStatus status;
}
