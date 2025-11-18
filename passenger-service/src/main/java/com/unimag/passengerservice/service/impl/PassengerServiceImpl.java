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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @Override
    public PassengerResponseDTO getById(Long id) {
        return passengerRepository.findById(id).map(passengerMapper::toDTO).orElseThrow(() -> new PassengerNotFoundException("Passenger not found with id: " + id));
    }

    @Override
    public List<PassengerResponseDTO> getAll() {
        return passengerRepository.findAll().stream().map(passengerMapper::toDTO).toList();
    }

    @Override
    public PassengerResponseDTO create(CreatePassengerRequestDTO request) {
        return passengerMapper.toDTO(passengerRepository.save(passengerMapper.toEntity(request)));
    }

    @Override
    public PassengerResponseDTO update(Long id, UpdatePassengerRequestDTO request) {
        Passenger passenger = passengerRepository.findById(id).orElseThrow(() -> new PassengerNotFoundException("Passenger not found with id: " + id));

        if(request.name()!=null) {
            passenger.setName(request.name());
        }
        if(request.email()!=null) {
            passenger.setEmail(request.email());
        }

        return passengerMapper.toDTO(passengerRepository.save(passenger));
    }

    @Override
    public void delete(Long id) {
        if(!passengerRepository.existsById(id)) {
            throw new PassengerNotFoundException("Passenger not found with id: " + id);
        }

        passengerRepository.deleteById(id);
    }
}
