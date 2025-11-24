package com.unimag.paymentservice.exception.notFound;

public class PaymentIntentNotFound extends RuntimeException {
    public PaymentIntentNotFound(String message) {
        super(message);
    }
}
