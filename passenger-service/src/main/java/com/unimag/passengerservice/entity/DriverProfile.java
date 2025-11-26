package com.unimag.passengerservice.entity;

import com.unimag.passengerservice.entity.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "driver_profiles")
public class DriverProfile {

    @Id
    private Long id;
    private Passenger passenger;
    private String licenseNumber;
    private String carPlate;
    private Integer seatsOffered;
    private VerificationStatus verificationStatus;
}
