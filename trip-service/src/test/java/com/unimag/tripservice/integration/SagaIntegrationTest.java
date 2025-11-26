package com.unimag.tripservice.integration;

import com.unimag.tripservice.dto.event.PaymentFailed;
import com.unimag.tripservice.dto.reservation.CreateReservation;
import com.unimag.tripservice.dto.reservation.ResponseReservation;
import com.unimag.tripservice.entity.Reservation;
import com.unimag.tripservice.entity.Trip;
import com.unimag.tripservice.enums.ReservationStatus;
import com.unimag.tripservice.enums.TripStatus;
import com.unimag.tripservice.repositories.ReservationRepository;
import com.unimag.tripservice.repositories.TripRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;


import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@Slf4j
public class SagaIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("tripdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("schema.sql"); ;

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                String.format("r2dbc:postgresql://%s:%d/%s",
                        postgres.getHost(),
                        postgres.getFirstMappedPort(),
                        postgres.getDatabaseName())
        );
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
        registry.add("spring.cloud.stream.kafka.binder.brokers", kafka::getBootstrapServers);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ReservationRepository reservationRepository;


    @Autowired
    private StreamBridge streamBridge;  // Para publicar eventos manualmente


    private Trip testTrip;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll().block();
        tripRepository.deleteAll().block();

        testTrip = Trip.builder()
                .driverId(1L)
                .origin("Madrid")
                .destination("Barcelona")
                .startTime(java.time.LocalDateTime.now().plusDays(2))
                .seatsTotal(4)
                .seatsAvailable(4)
                .price(java.math.BigDecimal.valueOf(35.00))
                .status(TripStatus.SCHEDULED)
                .build();

        testTrip = tripRepository.save(testTrip).block();
    }


    @Test
    void testHappyPathSaga_ReservationSuccessWithPayment() {
        // Step 1: Create reservation
        CreateReservation request = CreateReservation.builder()
                .tripId(testTrip.getId())
                .passengerId(100L)
                .build();

        webTestClient.post()
                .uri("/api/reservations")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseReservation.class)  // ✅ Cambiar a ResponseReservation
                .value(reservation -> {
                    assertThat(reservation.id()).isNotNull();
                    assertThat(reservation.status()).isEqualTo(ReservationStatus.PENDING);
                });

        // Verify seats are decremented
        StepVerifier.create(tripRepository.findById(testTrip.getId()))
                .expectNextMatches(trip -> trip.getSeatsAvailable() == 3)
                .verifyComplete();

        // Verify reservation was created
        StepVerifier.create(reservationRepository.findByPassengerId(100L))
                .expectNextMatches(reservation -> {
                    assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
                    assertThat(reservation.getPassengerId()).isEqualTo(100L);
                    return true;
                })
                .verifyComplete();
    }



    @Test
    void testCompensationSaga_PaymentFailure() {
        // Step 1: Create reservation
        CreateReservation request = CreateReservation.builder()
                .tripId(testTrip.getId())
                .passengerId(200L)
                .build();

        ResponseReservation reservation = webTestClient.post()
                .uri("/api/reservations")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseReservation.class)
                .returnResult()
                .getResponseBody();

        assertThat(reservation).isNotNull();
        assertThat(reservation.status()).isEqualTo(ReservationStatus.PENDING);

        // Verify seats were decremented
        Trip tripAfterReservation = tripRepository.findById(testTrip.getId()).block();
        assertThat(tripAfterReservation.getSeatsAvailable()).isEqualTo(3);

        // Step 2: Simulate payment failure event
        PaymentFailed paymentFailed = PaymentFailed.builder()
                .reservationId(reservation.id())
                .reason("Insufficient funds")
                .build();

        // IMPORTANTE: Usa el binding correcto que coincida con tu configuración
        boolean sent = streamBridge.send("paymentFailed-in-0", paymentFailed);
        assertThat(sent).isTrue();

        log.info("PaymentFailed event sent: {}", paymentFailed);

        // Step 3: Wait for compensation to complete (aumenta el tiempo)
        await()
                .atMost(Duration.ofSeconds(15))  // Aumenta el timeout
                .pollInterval(Duration.ofMillis(500))
                .pollDelay(Duration.ofMillis(500))  // Espera inicial
                .untilAsserted(() -> {
                    Reservation r = reservationRepository.findById(reservation.id()).block();
                    assertThat(r).isNotNull();
                    log.info("Current reservation status: {}", r.getStatus());
                    assertThat(r.getStatus())
                            .isIn(ReservationStatus.PAYMENT_FAILED, ReservationStatus.CANCELLED);
                });

        // Step 4: Verify seats are restored
        Trip tripAfterCompensation = tripRepository.findById(testTrip.getId()).block();
        assertThat(tripAfterCompensation).isNotNull();
        assertThat(tripAfterCompensation.getSeatsAvailable()).isEqualTo(4);

        log.info("Test completed successfully - compensation worked!");
    }

}
