package com.unimag.passengerservice.service;

import com.unimag.passengerservice.dto.request.CreateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.request.UpdateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.response.DriverProfileResponseDTO;
import com.unimag.passengerservice.entity.DriverProfile;
import com.unimag.passengerservice.entity.Passenger;
import com.unimag.passengerservice.entity.enums.VerificationStatus;
import com.unimag.passengerservice.exception.alreadyexists.DriverProfileAlreadyExistsException;
import com.unimag.passengerservice.exception.notfound.DriverProfileNotFoundException;
import com.unimag.passengerservice.exception.notfound.PassengerNotFoundException;
import com.unimag.passengerservice.mapper.DriverProfileMapper;
import com.unimag.passengerservice.repository.DriverProfileRepository;
import com.unimag.passengerservice.repository.PassengerRepository;
import com.unimag.passengerservice.service.impl.DriverProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverProfileServiceTest {

    @Mock
    private DriverProfileRepository driverProfileRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private DriverProfileMapper driverProfileMapper;

    @InjectMocks
    private DriverProfileServiceImpl driverProfileService;

    private DriverProfile driverProfile;
    private Passenger passenger;
    private DriverProfileResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        passenger = new Passenger();
        passenger.setId(10L);

        driverProfile = new DriverProfile();
        driverProfile.setId(1L);
        driverProfile.setPassenger(passenger);
        driverProfile.setLicenseNumber("ABC123");
        driverProfile.setVerificationStatus(VerificationStatus.PENDING);

        responseDTO = new DriverProfileResponseDTO(
                1L,
                passenger.getId(),
                "ABC123",
                "CAR123",
                3,
                VerificationStatus.PENDING
        );
    }

    @Test
    void getById_ShouldReturnProfile_WhenExists() {
        when(driverProfileRepository.findById(1L)).thenReturn(Mono.just(driverProfile));
        when(driverProfileMapper.toDTO(driverProfile)).thenReturn(responseDTO);

        StepVerifier.create(driverProfileService.getById(1L))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        when(driverProfileRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(driverProfileService.getById(1L))
                .expectError(DriverProfileNotFoundException.class)
                .verify();
    }

    @Test
    void getAll_ShouldReturnAllProfiles() {
        when(driverProfileRepository.findAll()).thenReturn(Flux.just(driverProfile));
        when(driverProfileMapper.toDTO(driverProfile)).thenReturn(responseDTO);

        StepVerifier.create(driverProfileService.getAll())
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void create_ShouldCreateProfile_WhenValid() {
        CreateDriverProfileRequestDTO request =
                new CreateDriverProfileRequestDTO(10L, "ABC123", "CAR123", 3);

        when(passengerRepository.findById(10L)).thenReturn(Mono.just(passenger));
        when(driverProfileRepository.existsByPassengerId(10L)).thenReturn(Mono.just(false));
        when(driverProfileMapper.toEntity(request)).thenReturn(driverProfile);
        when(driverProfileRepository.save(driverProfile)).thenReturn(Mono.just(driverProfile));
        when(driverProfileMapper.toDTO(driverProfile)).thenReturn(responseDTO);

        StepVerifier.create(driverProfileService.create(request))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void create_ShouldThrowException_WhenPassengerNotFound() {
        CreateDriverProfileRequestDTO request =
                new CreateDriverProfileRequestDTO(10L, "ABC123", "CAR123", 3);

        when(passengerRepository.findById(10L)).thenReturn(Mono.empty());

        StepVerifier.create(driverProfileService.create(request))
                .expectError(PassengerNotFoundException.class)
                .verify();
    }

    @Test
    void create_ShouldThrowException_WhenDriverProfileAlreadyExists() {
        CreateDriverProfileRequestDTO request =
                new CreateDriverProfileRequestDTO(10L, "ABC123", "CAR123", 3);

        when(passengerRepository.findById(10L)).thenReturn(Mono.just(passenger));
        when(driverProfileRepository.existsByPassengerId(10L)).thenReturn(Mono.just(true));

        StepVerifier.create(driverProfileService.create(request))
                .expectError(DriverProfileAlreadyExistsException.class)
                .verify();
    }

    @Test
    void update_ShouldUpdateProfile_WhenExists() {
        UpdateDriverProfileRequestDTO request =
                new UpdateDriverProfileRequestDTO("NEW123", "XYZ789", 4, VerificationStatus.VERIFIED);

        when(driverProfileRepository.findById(1L)).thenReturn(Mono.just(driverProfile));

        DriverProfile updated = new DriverProfile();
        updated.setId(1L);
        updated.setLicenseNumber("NEW123");
        updated.setCarPlate("XYZ789");
        updated.setSeatsOffered(4);
        updated.setVerificationStatus(VerificationStatus.VERIFIED);
        updated.setPassenger(passenger);

        when(driverProfileRepository.save(any(DriverProfile.class))).thenReturn(Mono.just(updated));
        when(driverProfileMapper.toDTO(updated)).thenReturn(responseDTO);

        StepVerifier.create(driverProfileService.update(1L, request))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void update_ShouldThrowException_WhenNotFound() {
        UpdateDriverProfileRequestDTO request =
                new UpdateDriverProfileRequestDTO(null, null, null, null);

        when(driverProfileRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(driverProfileService.update(1L, request))
                .expectError(DriverProfileNotFoundException.class)
                .verify();
    }

    @Test
    void delete_ShouldRemoveLinkAndDelete_WhenPassengerExists() {
        when(driverProfileRepository.findById(1L)).thenReturn(Mono.just(driverProfile));
        when(passengerRepository.save(passenger)).thenReturn(Mono.just(passenger));
        when(driverProfileRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(driverProfileService.delete(1L))
                .verifyComplete();

        // driverProfile.getPassenger() should be unlinked
        assertNull(passenger.getDriverProfile());
    }

    @Test
    void delete_ShouldDelete_WhenPassengerNull() {
        driverProfile.setPassenger(null);

        when(driverProfileRepository.findById(1L)).thenReturn(Mono.just(driverProfile));
        when(driverProfileRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(driverProfileService.delete(1L))
                .verifyComplete();
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {
        when(driverProfileRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(driverProfileService.delete(1L))
                .expectError(DriverProfileNotFoundException.class)
                .verify();
    }
}