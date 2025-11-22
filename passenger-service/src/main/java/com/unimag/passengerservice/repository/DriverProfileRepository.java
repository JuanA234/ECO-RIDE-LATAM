package com.unimag.passengerservice.repository;

import com.unimag.passengerservice.entity.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
    Boolean existsByPassengerId(Long passengerId);
}
