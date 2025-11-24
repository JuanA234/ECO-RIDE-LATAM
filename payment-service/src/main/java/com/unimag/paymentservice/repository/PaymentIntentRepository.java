package com.unimag.paymentservice.repository;

import com.unimag.paymentservice.entity.PaymentIntent;
import org.springframework.data.r2dbc.repository.R2dbcRepository;


public interface PaymentIntentRepository extends R2dbcRepository<PaymentIntent, Long> {
}
