package com.unimag.tripservice.exception;

public class CannotConfirmNonPendingReservation extends RuntimeException {
    public CannotConfirmNonPendingReservation(String message) {
        super(message);
    }
}
