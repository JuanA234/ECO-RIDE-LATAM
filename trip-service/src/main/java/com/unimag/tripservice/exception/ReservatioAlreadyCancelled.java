package com.unimag.tripservice.exception;

public class ReservatioAlreadyCancelled extends RuntimeException {
    public ReservatioAlreadyCancelled(String message) {
        super(message);
    }
}
