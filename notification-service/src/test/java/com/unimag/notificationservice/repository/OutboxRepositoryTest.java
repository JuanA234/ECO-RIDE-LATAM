package com.unimag.notificationservice.repository;

import com.unimag.notificationservice.NotificationServiceApplication;
import com.unimag.notificationservice.entity.Outbox;
import com.unimag.notificationservice.entity.enums.OutboxStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

@DataR2dbcTest
@Import({NotificationServiceApplication.class})
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OutboxRepositoryTest {

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
    private OutboxRepository outboxRepository;

    private static final String DEFAULT_EVENT_TYPE = "USER_CREATED";
    private static final String DEFAULT_PAYLOAD = "{\"userId\": 1, \"username\": \"test\"}";
    private static final OutboxStatus DEFAULT_STATUS = OutboxStatus.PENDING;
    private static final int DEFAULT_RETRIES = 0;

    @BeforeEach
    void cleanUp() {
        outboxRepository.deleteAll().block();
    }

    @Test
    void testSave_shouldCreateNewRecordAndAssignId() {
        // Given
        Outbox newOutbox = new Outbox(
                null,
                DEFAULT_EVENT_TYPE,
                DEFAULT_PAYLOAD,
                DEFAULT_STATUS,
                DEFAULT_RETRIES
        );

        // When
        Mono<Outbox> savedOutboxMono = outboxRepository.save(newOutbox);

        // Then - Verificamos que se haya asignado un ID y los campos sean correctos
        StepVerifier.create(savedOutboxMono)
                .expectNextMatches(outbox ->
                        outbox.getId() != null &&
                                DEFAULT_STATUS.equals(outbox.getStatus()) &&
                                outbox.getRetries().equals(DEFAULT_RETRIES)
                )
                .verifyComplete();
    }

    @Test
    void testFindById_shouldReturnExistingRecord() {
        // Given - Guardamos un registro y bloqueamos para obtener su ID
        Outbox existingOutbox = outboxRepository.save(new Outbox(
                null, "ORDER_PLACED", "{}", OutboxStatus.SENT, 1)
        ).block();
        Long existingId = Objects.requireNonNull(existingOutbox).getId();

        // When
        Mono<Outbox> foundOutboxMono = outboxRepository.findById(existingId);

        // Then
        StepVerifier.create(foundOutboxMono)
                .expectNextMatches(outbox ->
                        outbox.getId().equals(existingId) &&
                                "ORDER_PLACED".equals(outbox.getEventType()) &&
                                OutboxStatus.SENT.equals(outbox.getStatus())
                )
                .verifyComplete();
    }

    @Test
    void testFindById_shouldReturnEmptyMonoForNonExistingId() {
        // When
        Mono<Outbox> foundOutboxMono = outboxRepository.findById(999L);

        // Then
        StepVerifier.create(foundOutboxMono)
                .expectNextCount(0) // No debe emitir ningún elemento
                .verifyComplete();
    }

    @Test
    void testFindAll_shouldReturnAllRecords() {
        // Given -
        outboxRepository.save(new Outbox(null, "EVENT_A", "{}", DEFAULT_STATUS, 0)).block();
        outboxRepository.save(new Outbox(null, "EVENT_B", "{}", OutboxStatus.FAILED, 3)).block();
        outboxRepository.save(new Outbox(null, "EVENT_C", "{}", DEFAULT_STATUS, 0)).block();

        // When
        var allOutboxFlux = outboxRepository.findAll();

        // Then
        StepVerifier.create(allOutboxFlux)
                .expectNextCount(3) // Espera exactamente tres registros
                .verifyComplete();
    }

    @Test
    void testSave_shouldUpdateExistingRecord() {
        // Given
        Outbox originalOutbox = outboxRepository.save(new Outbox(
                null, DEFAULT_EVENT_TYPE, DEFAULT_PAYLOAD, DEFAULT_STATUS, 0)
        ).block();

        // Modificamos el estado y los reintentos
        originalOutbox.setStatus(OutboxStatus.SENT);
        originalOutbox.setRetries(1);

        // When
        Mono<Outbox> updatedOutboxMono = outboxRepository.save(originalOutbox);

        // Then
        StepVerifier.create(updatedOutboxMono)
                .expectNextMatches(outbox ->
                        outbox.getId().equals(originalOutbox.getId()) &&
                                OutboxStatus.SENT.equals(outbox.getStatus()) &&
                                outbox.getRetries() == 1
                )
                .verifyComplete();
    }

    @Test
    void testDeleteById_shouldRemoveRecordFromDatabase() {
        // Given
        Outbox existingOutbox = outboxRepository.save(new Outbox(
                null, "TO_BE_DELETED", "{}", DEFAULT_STATUS, 0)
        ).block();
        Long idToDelete = Objects.requireNonNull(existingOutbox).getId();

        // When
        Mono<Void> deleteOperation = outboxRepository.deleteById(idToDelete);

        // Then - La operación de eliminación debe completarse con éxito (Mono<Void>)
        StepVerifier.create(deleteOperation)
                .verifyComplete();

        // Verificación - Intentamos buscarlo para confirmar que fue eliminado
        StepVerifier.create(outboxRepository.findById(idToDelete))
                .expectNextCount(0)
                .verifyComplete();
    }
}