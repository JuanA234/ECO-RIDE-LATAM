package com.unimag.passengerservice.exception.notfound;

import com.unimag.passengerservice.exception.ResourceNotFoundException;

public class PassengerNotFoundException extends ResourceNotFoundException {
    public PassengerNotFoundException(String message) {
        super(message);
    }
}
