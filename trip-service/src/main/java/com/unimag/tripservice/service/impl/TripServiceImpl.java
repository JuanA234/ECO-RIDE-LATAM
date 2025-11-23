package com.unimag.tripservice.service.impl;


import com.unimag.tripservice.entity.Trip;
import com.unimag.tripservice.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    @Override
    public Mono<Trip> getTripById(String id) {
        return null;
    }
}
