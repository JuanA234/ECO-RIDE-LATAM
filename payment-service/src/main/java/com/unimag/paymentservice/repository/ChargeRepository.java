package com.unimag.paymentservice.repository;

import com.unimag.paymentservice.entity.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
}
