package com.unimag.notificationservice.service.impl;

import com.unimag.notificationservice.dto.request.CreateTemplateRequestDTO;
import com.unimag.notificationservice.dto.response.TemplateResponseDTO;
import com.unimag.notificationservice.dto.request.UpdateTemplateRequestDTO;
import com.unimag.notificationservice.entity.Template;
import com.unimag.notificationservice.exception.notfound.TemplateNotFoundException;
import com.unimag.notificationservice.mapper.TemplateMapper;
import com.unimag.notificationservice.repository.TemplateRepository;
import com.unimag.notificationservice.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    TemplateRepository templateRepository;
    TemplateMapper templateMapper;

    @Override
    public TemplateResponseDTO getById(Long id) {
        log.debug("Getting template by id: {}", id);
        return templateRepository.findById(id)
                .map(templateMapper::toDTO)
                .orElseThrow(() -> new TemplateNotFoundException("template not found with id: " + id));
    }

    @Override
    public List<TemplateResponseDTO> getAll() {
        log.debug("Getting all templates");
        return templateRepository.findAll()
                .stream()
                .map(templateMapper::toDTO)
                .toList();
    }

    @Override
    public TemplateResponseDTO create(CreateTemplateRequestDTO request) {
        log.debug("Creating template: {}", request);
        return templateMapper.toDTO(templateRepository.save(templateMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public TemplateResponseDTO update(Long id, UpdateTemplateRequestDTO request) {
        log.info("Updating template with id: {}", id);
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException("template not found with id: " + id));

        if(request.code()!=null) {
            template.setCode(request.code());
        }

        if(request.channel()!=null) {
            template.setChannel(request.channel());
        }

        if(request.subject()!=null) {
            template.setSubject(request.subject());
        }

        if(request.body()!=null) {
            template.setBody(request.body());
        }

        return templateMapper.toDTO(templateRepository.save(template));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting template with id: {}", id);
        if(!templateRepository.existsById(id)) {
            throw new TemplateNotFoundException("template not found with id: " + id);
        }

        templateRepository.deleteById(id);
    }
}
