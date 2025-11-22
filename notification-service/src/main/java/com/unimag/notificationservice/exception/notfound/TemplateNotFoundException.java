package com.unimag.notificationservice.exception.notfound;

import com.unimag.notificationservice.exception.ResourceNotFoundException;

public class TemplateNotFoundException extends ResourceNotFoundException {
    public TemplateNotFoundException(String message) {
        super(message);
    }
}
