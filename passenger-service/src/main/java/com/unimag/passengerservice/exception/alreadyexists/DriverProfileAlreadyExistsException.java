package com.unimag.passengerservice.exception.alreadyexists;

import com.unimag.passengerservice.exception.ResourceAlreadyExistsException;

public class DriverProfileAlreadyExistsException extends ResourceAlreadyExistsException {
    public DriverProfileAlreadyExistsException(String message) {
        super(message);
    }
}
