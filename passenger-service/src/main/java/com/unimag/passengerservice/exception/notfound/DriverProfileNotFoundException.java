package com.unimag.passengerservice.exception.notfound;

import com.unimag.passengerservice.exception.ResourceNotFoundException;

public class DriverProfileNotFoundException extends ResourceNotFoundException {
    public DriverProfileNotFoundException(String message) {
        super(message);
    }
}
