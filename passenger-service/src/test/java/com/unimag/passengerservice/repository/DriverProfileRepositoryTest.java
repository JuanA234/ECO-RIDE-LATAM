package com.unimag.passengerservice.repository;

import com.unimag.passengerservice.PassengerServiceApplication;
import com.unimag.passengerservice.entity.DriverProfile;
import com.unimag.passengerservice.entity.Passenger;
import com.unimag.passengerservice.entity.enums.VerificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Import({PassengerServiceApplication.class})
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DriverProfileRepositoryTest {

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
    private DriverProfileRepository driverProfileRepository;

    @Autowired
    private PassengerRepository passengerRepository; // Necesario para crear FK

    private Passenger testPassenger;
    private DriverProfile testDriverProfile;

    @BeforeEach
    void setUp() {

    }

    @Test
    void findById_shouldReturnDriverProfile_whenExists() {

    }

    @Test
    void findById_shouldReturnEmpty_whenDoesNotExist() {
        // Act
        Mono<DriverProfile> result = driverProfileRepository.findById(999L);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findAll_shouldReturnAllDriverProfiles() {

    }

    @Test
    void findAll_shouldReturnEmpty_whenNoDriverProfiles() {
        // Act
        Flux<DriverProfile> result = driverProfileRepository.findAll();

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void save_shouldCreateNewDriverProfile() {

    }

    @Test
    void save_shouldUpdateExistingDriverProfile() {

    }

    @Test
    void deleteById_shouldRemoveDriverProfile() {

    }

    @Test
    void deleteById_shouldNotThrowException_whenIdDoesNotExist() {
        // Act & Assert
        StepVerifier.create(driverProfileRepository.deleteById(999L))
                .verifyComplete();
    }

    @Test
    void existsByPassengerId_shouldReturnTrue_whenDriverProfileExists() {

    }

    @Test
    void existsByPassengerId_shouldReturnFalse_whenDriverProfileDoesNotExist() {
        // Act
        Boolean exists = driverProfileRepository.existsByPassengerId(999L).block();

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void save_shouldFailWithUniqueConstraint_whenDuplicatePassengerId() {

    }

    @Test
    void save_shouldFailWithUniqueConstraint_whenDuplicateLicenseNo() {

    }

    @Test
    void save_shouldFailWithForeignKeyConstraint_whenPassengerDoesNotExist() {
        Passenger passenger = Passenger.builder().id(999L).build();

        // Arrange
        DriverProfile invalidDriver = DriverProfile.builder()
                .passenger(passenger) // Passenger inexistente
                .licenseNumber("LIC99999")
                .carPlate("INV999")
                .seatsOffered(3)
                .verificationStatus(VerificationStatus.PENDING)
                .build();

        // Act & Assert
        StepVerifier.create(driverProfileRepository.save(invalidDriver))
                .expectError()
                .verify();
    }

    @Test
    void delete_shouldCascadeFromPassenger_whenPassengerIsDeleted() {

    }
}