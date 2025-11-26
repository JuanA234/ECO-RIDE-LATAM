package com.unimag.passengerservice.repository;

import com.unimag.passengerservice.PassengerServiceApplication;
import com.unimag.passengerservice.entity.Passenger;
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
class PassengerRepositoryTest {

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
    private PassengerRepository passengerRepository;

    private static final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Limpia la tabla antes de cada test
        passengerRepository.deleteAll().block();
    }

    @Test
    void testSave_shouldCreateNewPassenger() {
        // Given
        Passenger newPassenger = Passenger.builder()
                .keycloak_sub("sub_abc_123").name("Alice").email("alice@example.com")
                .ratingAvg(4.5).createdAt(NOW).build();

        // When
        Mono<Passenger> savedPassengerMono = passengerRepository.save(newPassenger);

        // Then
        StepVerifier.create(savedPassengerMono)
                .expectNextMatches(p -> p.getId() != null && "Alice".equals(p.getName()))
                .verifyComplete();
    }

    @Test
    void testFindById_shouldReturnExistingPassenger() {
        // Given
        Passenger existing = passengerRepository.save(Passenger.builder()
                .keycloak_sub("sub_test").name("Bob").email("bob@test.com")
                .ratingAvg(3.0).createdAt(NOW).build()).block();
        Long existingId = Objects.requireNonNull(existing).getId();

        // When
        Mono<Passenger> foundPassenger = passengerRepository.findById(existingId);

        // Then
        StepVerifier.create(foundPassenger)
                .expectNextMatches(p -> p.getName().equals("Bob") && p.getEmail().equals("bob@test.com"))
                .verifyComplete();
    }

    @Test
    void testFindAll_shouldReturnAllPassengers() {
        // Given
        passengerRepository.save(Passenger.builder().keycloak_sub("s1").name("C1").email("c1@test.com").ratingAvg(4.0).createdAt(NOW).build()).block();
        passengerRepository.save(Passenger.builder().keycloak_sub("s2").name("C2").email("c2@test.com").ratingAvg(5.0).createdAt(NOW).build()).block();

        // When
        var allPassengers = passengerRepository.findAll();

        // Then
        StepVerifier.create(allPassengers)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void testDeleteById_shouldRemovePassenger() {
        // Given
        Passenger existing = passengerRepository.save(Passenger.builder()
                .keycloak_sub("sub_del").name("Dave").email("dave@test.com")
                .ratingAvg(2.5).createdAt(NOW).build()).block();
        Long idToDelete = Objects.requireNonNull(existing).getId();

        // When
        Mono<Void> deleteOperation = passengerRepository.deleteById(idToDelete);

        // Then
        StepVerifier.create(deleteOperation).verifyComplete();
        StepVerifier.create(passengerRepository.findById(idToDelete)).expectNextCount(0).verifyComplete();
    }

    @Test
    void existsByEmail_shouldReturnTrueIfEmailExists() {
        // Given
        passengerRepository.save(Passenger.builder()
                .keycloak_sub("sub_e").name("Eve").email("eve@test.com")
                .ratingAvg(4.0).createdAt(NOW).build()).block();

        // When
        Mono<Boolean> existsMono = passengerRepository.existsByEmail("eve@test.com");

        // Then
        StepVerifier.create(existsMono)
                .expectNext(true)
                .verifyComplete();
    }
}