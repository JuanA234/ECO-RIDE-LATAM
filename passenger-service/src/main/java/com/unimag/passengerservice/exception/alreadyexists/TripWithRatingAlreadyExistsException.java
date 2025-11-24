package com.unimag.passengerservice.exception.alreadyexists;

import com.unimag.passengerservice.exception.ResourceAlreadyExistsException;

public class TripWithRatingAlreadyExistsException extends ResourceAlreadyExistsException {
    public TripWithRatingAlreadyExistsException(String message) {
        super(message);
    }
}
