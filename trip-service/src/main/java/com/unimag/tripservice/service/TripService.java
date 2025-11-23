package com.unimag.tripservice.service;

import com.unimag.tripservice.entity.Trip;
import reactor.core.publisher.Mono;

public interface TripService {

    Mono<Trip> getTripById(String id);
}
