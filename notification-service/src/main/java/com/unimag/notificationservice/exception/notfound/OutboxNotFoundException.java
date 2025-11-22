package com.unimag.notificationservice.exception.notfound;

import com.unimag.notificationservice.exception.ResourceNotFoundException;

public class OutboxNotFoundException extends ResourceNotFoundException {
    public OutboxNotFoundException(String message) {
        super(message);
    }
}
