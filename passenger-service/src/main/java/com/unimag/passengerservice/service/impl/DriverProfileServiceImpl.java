package com.unimag.passengerservice.service.impl;

import com.unimag.passengerservice.dto.request.CreateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.request.UpdateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.response.DriverProfileResponseDTO;
import com.unimag.passengerservice.entity.DriverProfile;
import com.unimag.passengerservice.entity.Passenger;
import com.unimag.passengerservice.entity.VerificationStatus;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverProfileServiceImpl implements DriverProfileService {

    private final DriverProfileRepository driverProfileRepository;
    private final PassengerRepository passengerRepository;
    private final DriverProfileMapper driverProfileMapper;


    @Override
    @Transactional(readOnly = true)
    public DriverProfileResponseDTO getById(Long id) {
        log.debug("Getting driver profile by id: {}", id);
        return driverProfileRepository.findById(id)
                .map(driverProfileMapper::toDTO)
                .orElseThrow(() -> new DriverProfileNotFoundException("Driver Profile not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverProfileResponseDTO> getAll() {
        log.debug("Getting all driver profiles");
        return driverProfileRepository.findAll()
                .stream()
                .map(driverProfileMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public DriverProfileResponseDTO create(CreateDriverProfileRequestDTO request) {
        DriverProfile driverProfile = driverProfileMapper.toEntity(request);

        Passenger passenger = passengerRepository.findById(request.passengerId())
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with id " + request.passengerId()));

        if (driverProfileRepository.existsByPassengerId(passenger.getId())) {
            throw new IllegalArgumentException("Driver Profile already exists with passenger id: " + request.passengerId());
        }

        driverProfile.setPassenger(passenger);
        driverProfile.setVerificationStatus(VerificationStatus.PENDING);

        log.info("Driver profile created with id: {} for passenger id: {}", driverProfile.getId(), request.passengerId());
        return driverProfileMapper.toDTO(driverProfileRepository.save(driverProfile));
    }

    @Override
    @Transactional
    public DriverProfileResponseDTO update(Long id, UpdateDriverProfileRequestDTO request) {
        log.info("Updating driver profile with id: {}", id);

        DriverProfile driverProfile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new DriverProfileNotFoundException("DriverProfile not found with id " + id));

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

        log.info("Driver profile updated successfully with id: {}", id);
        return driverProfileMapper.toDTO(driverProfileRepository
                .save(driverProfile));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Deleting driver profile with id: {}", id);

        DriverProfile driverProfile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new DriverProfileNotFoundException("DriverProfile not found with id " + id));

        Passenger passenger = driverProfile.getPassenger();
        if(passenger!=null){
            passenger.setDriverProfile(null);
        }

        driverProfileRepository.deleteById(id);
        log.info("Driver profile deleted successfully with id: {}", id);
    }

//    @Override
//    public DriverProfileResponseDTO getBySub(String keycloak_sub) {
//        Passenger passenger = passengerRepository.findByKeycloak_sub(keycloak_sub).orElseThrow(() -> new PassengerNotFoundException("Passenger not found with keycloak_sub: " + keycloak_sub));
//
//        DriverProfile driverProfile = passenger.getDriverProfile();
//        if (driverProfile == null) {
//            throw new DriverProfileNotFoundException(
//                    "DriverProfile not found for passenger with keycloak_sub: " + keycloak_sub
//            );
//        }
//        return driverProfileMapper.toDTO(driverProfile);
//    }
}
