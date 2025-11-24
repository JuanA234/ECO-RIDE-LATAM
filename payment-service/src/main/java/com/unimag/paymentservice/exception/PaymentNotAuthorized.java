package com.unimag.paymentservice.exception;

public class PaymentNotAuthorized extends RuntimeException {
    public PaymentNotAuthorized(String message) {
        super(message);
    }
}
