package com.unimag.paymentservice.repository;

import com.unimag.paymentservice.entity.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PaymentIntentRepository extends ReactiveCrudRepository<PaymentIntent, Long> {
}
