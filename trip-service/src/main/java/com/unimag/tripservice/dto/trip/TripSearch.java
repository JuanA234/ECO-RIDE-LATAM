package com.unimag.tripservice.dto.trip;

import java.time.LocalDateTime;

public record TripSearch(String origin, String destination, LocalDateTime startTime) {
}
