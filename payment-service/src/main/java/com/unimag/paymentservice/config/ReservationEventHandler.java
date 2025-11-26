package com.unimag.paymentservice.config;

import com.unimag.paymentservice.dto.event.PaymentAuthorized;
import com.unimag.paymentservice.dto.event.PaymentFailed;
import com.unimag.paymentservice.dto.event.ReservationCancelled;
import com.unimag.paymentservice.dto.event.ReservationRequested;
import com.unimag.paymentservice.enums.PaymentStatus;
import com.unimag.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ReservationEventHandler {
    private final PaymentService paymentService;
    private final StreamBridge streamBridge;

    /**
     * Step 1: Listen to ReservationRequested -> Authorize Payment
     */

    @Bean
    public Consumer<Flux<ReservationRequested>> reservationRequested(){
        return flux -> flux
                .flatMap(event ->{
                    log.info("Received ReservationRequestedEvent: {}", event);

                    return paymentService.authorizePayment(event.reservationId(), event.amount())
                            .flatMap(paymentIntent -> {
                                if (paymentIntent.paymentStatus() == PaymentStatus.AUTHORIZED){
                                    // Happy path: Payment authorized
                                    PaymentAuthorized authorizedEvent = PaymentAuthorized.builder()
                                            .reservationId(paymentIntent.reservationId())
                                            .paymentId(paymentIntent.id())
                                            .chargeId(new Random().nextLong())
                                            .build();

                                    streamBridge.send("paymentAuthorized-out-0", authorizedEvent);
                                    log.info("PaymentAuthorized event published: {}", authorizedEvent);

                                    return Mono.just(authorizedEvent);
                                }else{
                                    // Sad path: Payment failed

                                    PaymentFailed failedEvent = PaymentFailed.builder()
                                            .reservationId(paymentIntent.reservationId())
                                            .reason("Payment failed")
                                            .build();

                                    streamBridge.send("paymentFailed-out-0", failedEvent);
                                    log.warn("PaymentFailed event published: {}", failedEvent);

                                    return Mono.just(failedEvent);
                                }
                            })
                            .onErrorResume(error -> {
                                log.error("Error authorizing payment: {}", error.getMessage());

                                PaymentFailed failedEvent = PaymentFailed.builder()
                                        .reservationId(event.reservationId())
                                        .reason("Payment processing error: " + error.getMessage())
                                        .build();
                                streamBridge.send("paymentFailed-out-0", failedEvent);
                                return Mono.empty();
                            });
                })
                .subscribe();
    }

    /**
     * Compensation: Listen to ReservationCancelled -> Refund Payment
     */
    @Bean
    public Consumer<Flux<ReservationCancelled>> reservationCancelled(){

        return flux -> flux
                .flatMap(event ->{
                    log.info("Received ReservationCancelledEvent: {}", event);

                    return paymentService.refundPayment(event.reservationId(), event.reason())
                            .doOnSuccess(refund ->
                                log.info("Refund processed for reservation: {}, refund id: {}",
                                        event.reservationId(), refund.id())
                            )
                            .onErrorResume(error ->{
                                log.error("Error refunding payment: {}", error.getMessage());
                                return Mono.empty();
                            });
                })
                .subscribe();

    }
}
