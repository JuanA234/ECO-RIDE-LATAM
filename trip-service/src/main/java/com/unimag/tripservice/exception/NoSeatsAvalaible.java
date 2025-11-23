package com.unimag.tripservice.exception;

public class NoSeatsAvalaible extends RuntimeException {
    public NoSeatsAvalaible(String message) {
        super(message);
    }
}
