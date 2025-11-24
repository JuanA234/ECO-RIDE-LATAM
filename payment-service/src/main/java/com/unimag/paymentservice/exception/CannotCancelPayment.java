package com.unimag.paymentservice.exception;

public class CannotCancelPayment extends RuntimeException {
    public CannotCancelPayment(String message) {
        super(message);
    }
}
