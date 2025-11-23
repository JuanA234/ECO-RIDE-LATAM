package com.unimag.paymentservice.repository;

import com.unimag.paymentservice.entity.Charge;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ChargeRepository extends R2dbcRepository<Charge, Long> {
}
