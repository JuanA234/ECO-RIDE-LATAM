package com.unimag.tripservice.exception;

public class CannotReleaseMoreSeats extends RuntimeException {
    public CannotReleaseMoreSeats(String message) {
        super(message);
    }
}
