package com.unimag.paymentservice.exception.notFound;

public class ChargeNotFound extends RuntimeException {
    public ChargeNotFound(String message) {
        super(message);
    }
}
