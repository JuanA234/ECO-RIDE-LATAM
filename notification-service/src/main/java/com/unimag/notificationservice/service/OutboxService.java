package com.unimag.notificationservice.service;


import com.unimag.notificationservice.dto.response.OutboxResponseDTO;

import java.util.List;

public interface OutboxService {
    OutboxResponseDTO getById(Long id);
    List<OutboxResponseDTO> getAll();
    OutboxResponseDTO retry(Long id);
    void delete(Long id);
}
