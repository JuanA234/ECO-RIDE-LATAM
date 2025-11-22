package com.unimag.tripservice.exception;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;


@Builder
public record ApiError(
        LocalDateTime timestamp,
        Integer status,
        String message,
        Map<String, String> errors// Usado para errores de validación, puede ser null
) {

}
