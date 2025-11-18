package com.unimag.passengerservice.service;

import com.unimag.passengerservice.dto.request.CreatePassengerRequestDTO;
import com.unimag.passengerservice.dto.request.UpdatePassengerRequestDTO;
import com.unimag.passengerservice.dto.response.PassengerResponseDTO;

import java.util.List;

public interface PassengerService {
    PassengerResponseDTO getById(Long id);
    List<PassengerResponseDTO> getAll();
    PassengerResponseDTO create(CreatePassengerRequestDTO request);
    PassengerResponseDTO update(Long id, UpdatePassengerRequestDTO request);
    void delete(Long id);
}
