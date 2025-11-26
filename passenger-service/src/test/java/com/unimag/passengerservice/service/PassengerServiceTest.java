package com.unimag.passengerservice.service;

import com.unimag.passengerservice.dto.request.CreatePassengerRequestDTO;
import com.unimag.passengerservice.dto.request.UpdatePassengerRequestDTO;
import com.unimag.passengerservice.dto.response.PassengerResponseDTO;
import com.unimag.passengerservice.entity.Passenger;
import com.unimag.passengerservice.exception.alreadyexists.PassengerWithEmailAlreadyExistsException;
import com.unimag.passengerservice.exception.notfound.PassengerNotFoundException;
import com.unimag.passengerservice.mapper.PassengerMapper;
import com.unimag.passengerservice.repository.PassengerRepository;
import com.unimag.passengerservice.service.impl.PassengerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    private Passenger passenger;
    private PassengerResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        passenger = new Passenger();
        passenger.setId(1L);
        passenger.setName("Arturo");
        passenger.setEmail("arturo@example.com");

        responseDTO = new PassengerResponseDTO(
                1L,
                "Arturo",
                "arturo@example.com",
                0.0,
                LocalDateTime.now()
        );
    }

    @Test
    void getById_ShouldReturnPassenger_WhenExists() {
        when(passengerRepository.findById(1L)).thenReturn(Mono.just(passenger));
        when(passengerMapper.toDTO(passenger)).thenReturn(responseDTO);

        StepVerifier.create(passengerService.getById(1L))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        when(passengerRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(passengerService.getById(1L))
                .expectError(PassengerNotFoundException.class)
                .verify();
    }

    @Test
    void getAll_ShouldReturnAllPassengers() {
        when(passengerRepository.findAll()).thenReturn(Flux.just(passenger));
        when(passengerMapper.toDTO(passenger)).thenReturn(responseDTO);

        StepVerifier.create(passengerService.getAll())
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void create_ShouldCreatePassenger_WhenEmailNotExists() {
        CreatePassengerRequestDTO request =
                new CreatePassengerRequestDTO("Arturo", "arturo@example.com");

        when(passengerRepository.existsByEmail("arturo@example.com")).thenReturn(Mono.just(false));
        when(passengerMapper.toEntity(request)).thenReturn(passenger);
        when(passengerRepository.save(passenger)).thenReturn(Mono.just(passenger));
        when(passengerMapper.toDTO(passenger)).thenReturn(responseDTO);

        StepVerifier.create(passengerService.create(request))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void create_ShouldThrowException_WhenEmailAlreadyExists() {
        CreatePassengerRequestDTO request =
                new CreatePassengerRequestDTO("Arturo", "arturo@example.com");

        when(passengerRepository.existsByEmail("arturo@example.com"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(passengerService.create(request))
                .expectError(PassengerWithEmailAlreadyExistsException.class)
                .verify();
    }

    @Test
    void update_ShouldUpdatePassenger_WhenExists() {
        UpdatePassengerRequestDTO request =
                new UpdatePassengerRequestDTO("Nuevo Nombre", "nuevo@example.com");

        Passenger updated = new Passenger();
        updated.setId(1L);
        updated.setName("Nuevo Nombre");
        updated.setEmail("nuevo@example.com");

        when(passengerRepository.findById(1L)).thenReturn(Mono.just(passenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(Mono.just(updated));
        when(passengerMapper.toDTO(updated)).thenReturn(responseDTO);

        StepVerifier.create(passengerService.update(1L, request))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void update_ShouldThrowException_WhenNotFound() {
        UpdatePassengerRequestDTO request =
                new UpdatePassengerRequestDTO("X", "x@example.com");

        when(passengerRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(passengerService.update(1L, request))
                .expectError(PassengerNotFoundException.class)
                .verify();
    }

    @Test
    void delete_ShouldDeletePassenger_WhenExists() {
        when(passengerRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(passengerRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(passengerService.delete(1L))
                .verifyComplete();
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {
        when(passengerRepository.existsById(1L)).thenReturn(Mono.just(false));

        StepVerifier.create(passengerService.delete(1L))
                .expectError(PassengerNotFoundException.class)
                .verify();
    }
}