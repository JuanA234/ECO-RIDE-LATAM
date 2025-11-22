package com.unimag.notificationservice.service;

import com.unimag.notificationservice.dto.request.CreateTemplateRequestDTO;
import com.unimag.notificationservice.dto.response.TemplateResponseDTO;
import com.unimag.notificationservice.dto.request.UpdateTemplateRequestDTO;

import java.util.List;

public interface TemplateService {
    TemplateResponseDTO getById(Long id);
    List<TemplateResponseDTO> getAll();
    TemplateResponseDTO create(CreateTemplateRequestDTO createTemplateRequestDTO);
    TemplateResponseDTO update(Long id, UpdateTemplateRequestDTO updateTemplateRequestDTO);
    void delete(Long id);
}
