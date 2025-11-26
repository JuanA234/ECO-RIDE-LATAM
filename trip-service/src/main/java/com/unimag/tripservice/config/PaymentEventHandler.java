package com.unimag.tripservice.config;


import com.unimag.tripservice.dto.event.PaymentAuthorized;
import com.unimag.tripservice.dto.event.PaymentFailed;
import com.unimag.tripservice.dto.event.ReservationCancelled;
import com.unimag.tripservice.dto.event.ReservationConfirmed;
import com.unimag.tripservice.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final TripService tripService;
    private final StreamBridge streamBridge;

    /**
     * Happy Path: Payment authorized -> Confirm reservation
     */
    @Bean
    public Consumer<Flux<PaymentAuthorized>> paymentAuthorized(){
        return flux -> flux
                .flatMap(event -> {
                    log.info("Received PaymentAuthorizedEvent: {}", event);

                    return tripService.confirmReservation(event.reservationId())
                            .flatMap(responseReservation -> {

                                ReservationConfirmed confirmedEvent = ReservationConfirmed.builder()
                                        .reservationId(responseReservation.id())
                                        .build();

                                streamBridge.send("reservationConfirmed-out-0", confirmedEvent);
                                log.info("ReservationConfirmed event published: {}", confirmedEvent);
                                return Mono.just(responseReservation);
                            }).onErrorResume(error -> {
                                log.error("Error confirming reservation: {}", error.getMessage());
                                return Mono.empty();
                            });
                }).subscribe();
    }

    /**
     * Compensation Path: Payment failed -> Cancel reservation (release seat)
     */
    @Bean
    public Consumer<Flux<PaymentFailed>> paymentFailed(){
        return flux -> flux
                .flatMap(event -> {
                    log.info("Received PaymentFailedEvent: {}", event);

                    return tripService.markReservationPaymentFailed(event.reservationId())
                            .flatMap(responseReservation -> {

                                ReservationCancelled confirmedEvent = ReservationCancelled.builder()
                                        .reservationId(responseReservation.id())
                                        .reason("Payment failed: " + event.reason())
                                        .build();

                                streamBridge.send("reservationCancelled-out-0", confirmedEvent);
                                log.info("ReservationCancelled  event published (compensation): {}", confirmedEvent);
                                return Mono.just(responseReservation);
                            }).onErrorResume(error -> {
                                log.error("Error cancelling reservation after payment failure: {}", error.getMessage());
                                return Mono.empty();
                            });
                }).subscribe();
    }
}
