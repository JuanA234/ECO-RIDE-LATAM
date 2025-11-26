package com.unimag.passengerservice.service.impl;

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
import com.unimag.passengerservice.service.DriverProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverProfileServiceImpl implements DriverProfileService {

    private final DriverProfileRepository driverProfileRepository;
    private final PassengerRepository passengerRepository;
    private final DriverProfileMapper driverProfileMapper;


    @Override
    public Mono<DriverProfileResponseDTO> getById(Long id) {
        log.debug("Getting driver profile by id: {}", id);

        return driverProfileRepository.findById(id)
                .switchIfEmpty(Mono.error(new DriverProfileNotFoundException("Driver Profile not found with id: " + id)))
                .map(driverProfileMapper::toDTO);
    }

    @Override
    public Flux<DriverProfileResponseDTO> getAll() {
        log.debug("Getting all driver profiles");
        return driverProfileRepository.findAll()
                .map(driverProfileMapper::toDTO);
    }

    @Override
    @Transactional
    public Mono<DriverProfileResponseDTO> create(CreateDriverProfileRequestDTO request) {
        return passengerRepository.findById(request.passengerId())
                .switchIfEmpty(Mono.error(new PassengerNotFoundException("Passenger not found")))
                .flatMap(passenger ->
                        driverProfileRepository.existsByPassengerId(passenger.getId())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new DriverProfileAlreadyExistsException(
                                                "Driver Profile already exists with passenger id " + request.passengerId()
                                        ));
                                    }

                                    DriverProfile entity = driverProfileMapper.toEntity(request);
                                    entity.setPassenger(passenger);
                                    entity.setVerificationStatus(VerificationStatus.PENDING);

                                    return driverProfileRepository.save(entity);
                                })
                )
                .map(driverProfileMapper::toDTO);
    }

    @Override
    @Transactional
    public Mono<DriverProfileResponseDTO> update(Long id, UpdateDriverProfileRequestDTO request) {
        log.info("Updating driver profile with id: {}", id);

        return driverProfileRepository.findById(id)
                .switchIfEmpty(Mono.error(new DriverProfileNotFoundException("DriverProfile not found with id " + id)))
                .flatMap(driverProfile -> {
                    if(request.licenseNumber()!=null){
                        driverProfile.setLicenseNumber(request.licenseNumber());
                    }

                    if(request.carPlate()!=null){
                        driverProfile.setCarPlate(request.carPlate());
                    }

                    if(request.seatsOffered()!=null){
                        driverProfile.setSeatsOffered(request.seatsOffered());
                    }

                    if(request.verificationStatus()!=null){
                        driverProfile.setVerificationStatus(request.verificationStatus());
                    }

                    return driverProfileRepository.save(driverProfile);
                }).map(driverProfileMapper::toDTO);
    }

    @Override
    @Transactional
    public Mono<Void> delete(Long id) {
        log.warn("Deleting driver profile with id: {}", id);

        return driverProfileRepository.findById(id).
                switchIfEmpty(Mono.error(new DriverProfileNotFoundException("Driver Profile not found from passenger with id " + id)))
                .flatMap(profile -> {
                    Passenger passenger = profile.getPassenger();
                    if (passenger != null) {
                        passenger.setDriverProfile(null);
                        return passengerRepository.save(passenger)
                                .then(driverProfileRepository.deleteById(id));
                    }
                    return driverProfileRepository.deleteById(id);
                });
    }
}
