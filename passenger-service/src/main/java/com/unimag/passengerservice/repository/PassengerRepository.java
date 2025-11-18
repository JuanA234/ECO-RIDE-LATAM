package com.unimag.passengerservice.repository;

import com.unimag.passengerservice.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    //Optional<Passenger> findByKeycloak_sub(String keycloak_sub); //idk
}
