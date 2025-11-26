package com.unimag.notificationservice.config;

import com.unimag.notificationservice.dto.events.*;
import com.unimag.notificationservice.dto.request.SendNotificationRequestDTO;
import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import com.unimag.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationService notificationService;

    /**
     * Listener: ReservationConfirmed → Send confirmation email
     */
    @Bean
    public Consumer<Flux<ReservationConfirmedEvent>> reservationConfirmed() {
        return flux -> flux
                .flatMap(event -> {
                    log.info("Received ReservationConfirmedEvent: {}", event);

                    SendNotificationRequestDTO request = new SendNotificationRequestDTO(
                            "reservation-confirmed",
                            event.passengerEmail(),
                            Map.of(
                                    "passengerName", event.passengerName(),
                                    "tripId", event.tripId().toString(),
                                    "origin", event.origin(),
                                    "destination", event.destination(),
                                    "startTime", event.startTime().toString()
                            )
                    );

                    return notificationService.send(request)
                            .doOnSuccess(response ->
                                    log.info("Confirmation notification sent for reservation {}", event.reservationId())
                            )
                            .onErrorResume(error -> {
                                log.error("Failed to send confirmation notification for reservation {}: {}",
                                        event.reservationId(), error.getMessage());
                                return Mono.empty();
                            });
                })
                .subscribe();
    }

    /**
     * Listener: ReservationCancelled → Send cancellation email
     */
    @Bean
    public Consumer<Flux<ReservationCancelledEvent>> reservationCancelled() {
        return flux -> flux
                .flatMap(event -> {
                    log.info("Received ReservationCancelledEvent: {}", event);

                    SendNotificationRequestDTO request = new SendNotificationRequestDTO(
                            "reservation-cancelled",
                            event.passengerEmail(),
                            Map.of(
                                    "passengerName", event.passengerName(),
                                    "tripId", event.tripId().toString(),
                                    "reason", event.reason()
                            )
                    );

                    return notificationService.send(request)
                            .doOnSuccess(response ->
                                    log.info("Cancellation notification sent for reservation {}", event.reservationId())
                            )
                            .onErrorResume(error -> {
                                log.error("Failed to send cancellation notification: {}", error.getMessage());
                                return Mono.empty();
                            });
                })
                .subscribe();
    }

    /**
     * Listener: PaymentAuthorized → Send payment confirmation email
     */
    @Bean
    public Consumer<Flux<PaymentAuthorizedEvent>> paymentAuthorized() {
        return flux -> flux
                .flatMap(event -> {
                    log.info("Received PaymentAuthorizedEvent: {}", event);

                    SendNotificationRequestDTO request = new SendNotificationRequestDTO(
                            "payment-confirmed",
                            event.passengerEmail(),
                            Map.of(
                                    "passengerName", event.passengerName(),
                                    "amount", event.amount().toString(),
                                    "currency", event.currency(),
                                    "chargeId", event.chargeId().toString()
                            )
                    );

                    return notificationService.send(request)
                            .doOnSuccess(response ->
                                    log.info("Payment confirmation sent for reservation {}", event.reservationId())
                            )
                            .onErrorResume(error -> {
                                log.error("Failed to send payment confirmation: {}", error.getMessage());
                                return Mono.empty();
                            });
                })
                .subscribe();
    }

    /**
     * Listener: PaymentFailed → Send payment failure email
     */
    @Bean
    public Consumer<Flux<PaymentFailedEvent>> paymentFailed() {
        return flux -> flux
                .flatMap(event -> {
                    log.info("Received PaymentFailedEvent: {}", event);

                    SendNotificationRequestDTO request = new SendNotificationRequestDTO(
                            "payment-failed",
                            event.passengerEmail(),
                            Map.of(
                                    "passengerName", event.passengerName(),
                                    "reason", event.reason()
                            )
                    );

                    return notificationService.send(request)
                            .doOnSuccess(response ->
                                    log.info("Payment failure notification sent for reservation {}", event.reservationId())
                            )
                            .onErrorResume(error -> {
                                log.error("Failed to send payment failure notification: {}", error.getMessage());
                                return Mono.empty();
                            });
                })
                .subscribe();
    }

    /**
     * Listener: TripCompleted → Send trip completion emails
     */
    @Bean
    public Consumer<Flux<TripCompletedEvent>> tripCompleted() {
        return flux -> flux
                .flatMap(event -> {
                    log.info("Received TripCompletedEvent: {}", event);

                    // Notify driver
                    SendNotificationRequestDTO driverRequest = new SendNotificationRequestDTO(
                            "trip-completed-driver",
                            event.driverEmail(),
                            Map.of(
                                    "driverName", event.driverName(),
                                    "tripId", event.tripId().toString(),
                                    "completedAt", event.completedAt().toString()
                            )
                    );

                    Flux<OutboxResponseDTO> driverNotification = notificationService.send(driverRequest)
                            .doOnSuccess(response ->
                                    log.info("Trip completion notification sent to driver for trip {}", event.tripId())
                            )
                            .flux();

                    // Notify passengers
                    Flux<Object> passengerNotifications = Flux.fromIterable(event.passengers())
                            .flatMap(passenger -> {
                                SendNotificationRequestDTO passengerRequest = new SendNotificationRequestDTO(
                                        "trip-completed-passenger",
                                        passenger.email(),
                                        Map.of(
                                                "passengerName", passenger.name(),
                                                "tripId", event.tripId().toString(),
                                                "driverName", event.driverName()
                                        )
                                );

                                return notificationService.send(passengerRequest)
                                        .doOnSuccess(response ->
                                                log.info("Trip completion notification sent to passenger {} for trip {}",
                                                        passenger.passengerId(), event.tripId())
                                        );
                            });

                    // Send all notifications
                    return Flux.merge(driverNotification, passengerNotifications)
                            .onErrorResume(error -> {
                                log.error("Failed to send trip completion notifications: {}", error.getMessage());
                                return Flux.empty();
                            });
                })
                .subscribe();
    }
}
