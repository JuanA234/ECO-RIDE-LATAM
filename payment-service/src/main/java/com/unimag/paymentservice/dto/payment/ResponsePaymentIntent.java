package com.unimag.paymentservice.dto.payment;

import com.unimag.paymentservice.entity.PaymentIntent;
import com.unimag.paymentservice.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ResponsePaymentIntent(Long id,
                                    Long reservationId,
                                    BigDecimal amount,
                                    String currency,
                                    PaymentStatus paymentStatus) {
}
