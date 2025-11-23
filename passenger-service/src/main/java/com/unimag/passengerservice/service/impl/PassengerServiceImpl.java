package com.unimag.passengerservice.service.impl;

import com.unimag.passengerservice.dto.request.CreatePassengerRequestDTO;
import com.unimag.passengerservice.dto.request.UpdatePassengerRequestDTO;
import com.unimag.passengerservice.dto.response.PassengerResponseDTO;
import com.unimag.passengerservice.entity.Passenger;
import com.unimag.passengerservice.exception.notfound.PassengerNotFoundException;
import com.unimag.passengerservice.mapper.PassengerMapper;
import com.unimag.passengerservice.repository.PassengerRepository;
import com.unimag.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @Override
    public PassengerResponseDTO getById(Long id) {
        log.debug("Getting passenger by id: {}", id);

        return passengerRepository.findById(id)
                .map(passengerMapper::toDTO)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with id: " + id));
    }

    @Override
    public List<PassengerResponseDTO> getAll() {
        log.debug("Getting all passengers");

        return passengerRepository.findAll()
                .stream()
                .map(passengerMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public PassengerResponseDTO create(CreatePassengerRequestDTO request) {
        log.info("Creating passenger with email: {}", request.email());

        if (passengerRepository.existsByEmail(request.email())){
            log.warn("Passenger with email: {} already exists", request.email());
            throw new IllegalArgumentException("Email already registered");
        }

        Passenger passenger = passengerMapper.toEntity(request);
        Passenger saved = passengerRepository.save(passenger);

        log.info("Passenger created with id: {}", saved.getId());
        return passengerMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public PassengerResponseDTO update(Long id, UpdatePassengerRequestDTO request) {
        log.info("Updating passenger with id: {}", id);
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found with id: " + id));

        if(request.name()!=null) {
            passenger.setName(request.name());
        }
        if(request.email()!=null) {
            passenger.setEmail(request.email());
        }

        log.info("Passenger updated successfully with id: {}", id);
        return passengerMapper.toDTO(passengerRepository.save(passenger));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Deleting passenger with id: {}", id);

        if(!passengerRepository.existsById(id)) {
            throw new PassengerNotFoundException("Passenger not found with id: " + id);
        }

        passengerRepository.deleteById(id);
        log.info("Driver profile deleted successfully with id: {}", id);
    }
}
