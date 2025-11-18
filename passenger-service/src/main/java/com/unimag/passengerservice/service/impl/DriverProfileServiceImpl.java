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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverProfileServiceImpl implements DriverProfileService {

    private final DriverProfileRepository driverProfileRepository;
    private final PassengerRepository passengerRepository;
    private final DriverProfileMapper driverProfileMapper;


    @Override
    public DriverProfileResponseDTO getById(Long id) {
        return driverProfileRepository.findById(id).map(driverProfileMapper::toDTO).orElseThrow(() -> new DriverProfileNotFoundException("Driver Profile not found with id: " + id));
    }

    @Override
    public List<DriverProfileResponseDTO> getAll() {
        return driverProfileRepository.findAll().stream().map(driverProfileMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public DriverProfileResponseDTO create(CreateDriverProfileRequestDTO request) {
        DriverProfile driverProfile = driverProfileMapper.toEntity(request);

        Passenger passenger = passengerRepository.findById(request.passengerId()).orElseThrow(() -> new PassengerNotFoundException("Passenger not found with id " + request.passengerId()));

        driverProfile.setPassenger(passenger);
        driverProfile.setVerificationStatus(VerificationStatus.PENDING);

        return driverProfileMapper.toDTO(driverProfileRepository.save(driverProfile));
    }

    @Override
    public DriverProfileResponseDTO update(Long id, UpdateDriverProfileRequestDTO request) {
        DriverProfile driverProfile = driverProfileRepository.findById(id).orElseThrow(() -> new DriverProfileNotFoundException("DriverProfile not found with id " + id));

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

        return driverProfileMapper.toDTO(driverProfileRepository.save(driverProfile));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        DriverProfile driverProfile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new DriverProfileNotFoundException("DriverProfile not found with id " + id));

        Passenger passenger = driverProfile.getPassenger();
        if(passenger!=null){
            passenger.setDriverProfile(null);
        }

        driverProfileRepository.deleteById(id);
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
