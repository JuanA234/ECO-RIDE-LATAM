package com.unimag.passengerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "passengers")
public class Passenger {

    @Id
    private Long id;
    private String keycloak_sub;
    private String name;
    private String email;
    private Double ratingAvg;
    private LocalDateTime createdAt;
    private DriverProfile driverProfile;

}
