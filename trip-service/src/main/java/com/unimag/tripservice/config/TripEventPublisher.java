package com.unimag.tripservice.config;


import com.unimag.tripservice.dto.event.ReservationCancelled;
import com.unimag.tripservice.dto.event.ReservationConfirmed;
import com.unimag.tripservice.dto.event.ReservationRequested;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripEventPublisher{

    private final StreamBridge streamBridge;

    public void publishReservationRequested(ReservationRequested event) {
        streamBridge.send("reservationRequested-out-0", event);
        log.info("Published ReservationRequestedEvent: {}", event);
    }

    public void publishReservationConfirmed(ReservationConfirmed event) {
        streamBridge.send("reservationConfirmed-out-0", event);
        log.info("Published ReservationConfirmedEvent: {}", event);
    }

    public void publishReservationCancelled(ReservationCancelled event) {
        streamBridge.send("reservationCancelled-out-0", event);
        log.info("Published ReservationCancelledEvent: {}", event);
    }
}
