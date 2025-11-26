package com.unimag.passengerservice.repository;

import com.unimag.passengerservice.PassengerServiceApplication;
import com.unimag.passengerservice.entity.Passenger;
import com.unimag.passengerservice.entity.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Import({PassengerServiceApplication.class})
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RatingRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // R2DBC - Usa las credenciales generadas automáticamente
        registry.add("spring.r2dbc.url", () ->
                String.format("r2dbc:postgresql://%s:%d/%s",
                        postgres.getHost(),
                        postgres.getFirstMappedPort(),
                        postgres.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);

        // Flyway (JDBC)
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    // IDs de pasajeros pre-creados
    private Long passenger1Id; // Evaluador
    private Long passenger2Id; // Evaluado (Receptor de la media)
    private Long passenger3Id; // Otro evaluador

    /**
     * Prepara datos iniciales: limpia tablas y crea pasajeros para FK.
     */
    @BeforeEach
    void setUp() {
        // Limpiar todas las tablas (Rating depende de Passenger)
        ratingRepository.deleteAll().block();
        passengerRepository.deleteAll().block();

        // 1. Crear pasajeros requeridos para las FK
        Passenger p1 = passengerRepository.save(
                Passenger.builder().keycloak_sub("sub1").name("User A").email("a@test.com").ratingAvg(0.0).createdAt(LocalDateTime.now()).build()
        ).block();
        Passenger p2 = passengerRepository.save(
                Passenger.builder().keycloak_sub("sub2").name("User B").email("b@test.com").ratingAvg(0.0).createdAt(LocalDateTime.now()).build()
        ).block();
        Passenger p3 = passengerRepository.save(
                Passenger.builder().keycloak_sub("sub3").name("User C").email("c@test.com").ratingAvg(0.0).createdAt(LocalDateTime.now()).build()
        ).block();

        passenger1Id = Objects.requireNonNull(p1).getId();
        passenger2Id = Objects.requireNonNull(p2).getId(); // Receptor de las calificaciones
        passenger3Id = Objects.requireNonNull(p3).getId();
    }

    @Test
    void testSave_shouldCreateNewRating() {
        // Given
        Rating newRating = Rating.builder()
                .tripId(100L).fromId(passenger1Id).toId(passenger2Id).score(5).comment("Great trip").build();

        // When
        Mono<Rating> savedRatingMono = ratingRepository.save(newRating);

        // Then
        StepVerifier.create(savedRatingMono)
                .expectNextMatches(r -> r.getId() != null && r.getScore() == 5 && r.getTripId().equals(100L))
                .verifyComplete();
    }

    @Test
    void testFindById_shouldReturnExistingRating() {
        // Given
        Rating existing = ratingRepository.save(Rating.builder()
                .tripId(101L).fromId(passenger1Id).toId(passenger2Id).score(4).comment("Good").build()).block();
        Long existingId = Objects.requireNonNull(existing).getId();

        // When
        Mono<Rating> foundRating = ratingRepository.findById(existingId);

        // Then
        StepVerifier.create(foundRating)
                .expectNextMatches(r -> r.getTripId().equals(101L))
                .verifyComplete();
    }

    @Test
    void testFindAll_shouldReturnAllRatings() {
        // Given
        ratingRepository.save(Rating.builder().tripId(200L).fromId(passenger1Id).toId(passenger2Id).score(3).build()).block();
        ratingRepository.save(Rating.builder().tripId(201L).fromId(passenger3Id).toId(passenger2Id).score(5).build()).block();

        // When
        var allRatings = ratingRepository.findAll();

        // Then
        StepVerifier.create(allRatings)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void testDeleteById_shouldRemoveRating() {
        // Given
        Rating existing = ratingRepository.save(Rating.builder()
                .tripId(300L).fromId(passenger1Id).toId(passenger2Id).score(2).build()).block();
        Long idToDelete = Objects.requireNonNull(existing).getId();

        // When
        Mono<Void> deleteOperation = ratingRepository.deleteById(idToDelete);

        // Then
        StepVerifier.create(deleteOperation).verifyComplete();
        StepVerifier.create(ratingRepository.findById(idToDelete)).expectNextCount(0).verifyComplete();
    }

    @Test
    void findAverageScoreByPassengerId_shouldCalculateCorrectAverage() {
        // Given: Calificamos al passenger2Id
        ratingRepository.save(Rating.builder().tripId(401L).fromId(passenger1Id).toId(passenger2Id).score(5).build()).block(); // Score 5
        ratingRepository.save(Rating.builder().tripId(402L).fromId(passenger3Id).toId(passenger2Id).score(1).build()).block(); // Score 1
        // (5 + 1) / 2 = 3.0

        // When
        Mono<Double> averageScoreMono = ratingRepository.findAverageScoreByPassengerId(passenger2Id);

        // Then
        StepVerifier.create(averageScoreMono)
                .expectNext(3.0) // Verifica que la media sea 3.0
                .verifyComplete();
    }

    @Test
    void findAverageScoreByPassengerId_shouldReturnNullIfNoRatings() {
        // When
        // El pasajero 1 (passenger1Id) no tiene calificaciones recibidas.
        Mono<Double> averageScoreMono = ratingRepository.findAverageScoreByPassengerId(passenger1Id);

        // Then
        // Si la consulta AVG(score) sobre cero filas resulta en Mono.empty() (sin emisión),
        // esta es la verificación correcta:
        StepVerifier.create(averageScoreMono)
                .verifyComplete(); // Espera que el Mono termine sin emitir NINGÚN valor.
    }

    @Test
    void existsByTripIdAndFromIdAndToId_shouldReturnTrueIfExists() {
        // Given
        ratingRepository.save(Rating.builder().tripId(500L).fromId(passenger1Id).toId(passenger2Id).score(4).build()).block();

        // When
        Mono<Boolean> existsMono = ratingRepository.existsByTripIdAndFromIdAndToId(500L, passenger1Id, passenger2Id);

        // Then
        StepVerifier.create(existsMono)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByTripIdAndFromIdAndToId_shouldReturnFalseIfNotExists() {
        // When
        // La combinación de IDs no existe
        Mono<Boolean> existsMono = ratingRepository.existsByTripIdAndFromIdAndToId(999L, passenger1Id, passenger2Id);

        // Then
        StepVerifier.create(existsMono)
                .expectNext(false)
                .verifyComplete();
    }
}