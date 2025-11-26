package com.unimag.notificationservice.dto.events;

import java.math.BigDecimal;

public record PaymentAuthorizedEvent(
        Long reservationId,
        Long paymentIntentId,
        Long chargeId,
        String passengerEmail,
        String passengerName,
        BigDecimal amount,
        String currency
) {}
