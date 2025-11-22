package com.unimag.paymentservice.repository;

import com.unimag.paymentservice.entity.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {
}
