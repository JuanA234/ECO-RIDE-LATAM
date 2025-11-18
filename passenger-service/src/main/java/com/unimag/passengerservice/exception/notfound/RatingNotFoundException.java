package com.unimag.passengerservice.exception.notfound;

import com.unimag.passengerservice.exception.ResourceNotFoundException;

public class RatingNotFoundException extends ResourceNotFoundException {
    public RatingNotFoundException(String message) {
        super(message);
    }
}
