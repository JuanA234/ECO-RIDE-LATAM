package com.unimag.passengerservice.exception.alreadyexists;

import com.unimag.passengerservice.exception.ResourceAlreadyExistsException;

public class PassengerWithEmailAlreadyExistsException extends ResourceAlreadyExistsException {
    public PassengerWithEmailAlreadyExistsException(String message) {
        super(message);
    }
}
