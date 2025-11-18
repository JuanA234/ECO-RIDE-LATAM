package com.unimag.passengerservice.service;

import com.unimag.passengerservice.dto.request.CreateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.request.UpdateDriverProfileRequestDTO;
import com.unimag.passengerservice.dto.response.DriverProfileResponseDTO;

import java.util.List;

public interface DriverProfileService {
    DriverProfileResponseDTO getById(Long id);
    List<DriverProfileResponseDTO> getAll();
    DriverProfileResponseDTO create(CreateDriverProfileRequestDTO request);
    DriverProfileResponseDTO update(Long id, UpdateDriverProfileRequestDTO request);
    void delete(Long id);
}
