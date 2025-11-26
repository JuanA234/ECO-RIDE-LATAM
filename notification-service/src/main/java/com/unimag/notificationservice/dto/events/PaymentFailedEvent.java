package com.unimag.notificationservice.dto.events;

public record PaymentFailedEvent(
        Long reservationId,
        Long paymentIntentId,
        String passengerEmail,
        String passengerName,
        String reason
) {}
