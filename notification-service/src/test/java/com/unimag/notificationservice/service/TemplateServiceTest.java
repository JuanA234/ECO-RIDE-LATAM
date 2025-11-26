package com.unimag.notificationservice.service;

import com.unimag.notificationservice.dto.request.CreateTemplateRequestDTO;
import com.unimag.notificationservice.dto.request.UpdateTemplateRequestDTO;
import com.unimag.notificationservice.dto.response.TemplateResponseDTO;
import com.unimag.notificationservice.entity.Template;
import com.unimag.notificationservice.entity.enums.Channel;
import com.unimag.notificationservice.exception.notfound.TemplateNotFoundException;
import com.unimag.notificationservice.mapper.TemplateMapper;
import com.unimag.notificationservice.repository.TemplateRepository;
import com.unimag.notificationservice.service.impl.TemplateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private TemplateMapper templateMapper;

    @InjectMocks
    private TemplateServiceImpl templateService;

    private Template template;
    private TemplateResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        template = new Template();
        template.setId(1L);
        template.setCode("reservation-confirmed");
        template.setChannel(Channel.EMAIL);
        template.setSubject("Subject");
        template.setBody("Body");

        responseDTO = new TemplateResponseDTO(
                1L,
                "reservation-confirmed",
                Channel.EMAIL,
                "Subject",
                "Body"
        );
    }

    @Test
    void getById_ShouldReturnTemplate_WhenExists() {
        when(templateRepository.findById(1L)).thenReturn(Mono.just(template));
        when(templateMapper.toDTO(template)).thenReturn(responseDTO);

        StepVerifier.create(templateService.getById(1L))
                .expectNext(responseDTO)
                .verifyComplete();

        verify(templateRepository).findById(1L);
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        when(templateRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(templateService.getById(1L))
                .expectErrorMatches(ex ->
                        ex instanceof TemplateNotFoundException &&
                                ex.getMessage().contains("template not found with id")
                )
                .verify();
    }

    @Test
    void getAll_ShouldReturnListOfTemplates() {
        when(templateRepository.findAll()).thenReturn(Flux.just(template));
        when(templateMapper.toDTO(template)).thenReturn(responseDTO);

        StepVerifier.create(templateService.getAll())
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void create_ShouldSaveTemplateAndReturnDTO() {
        CreateTemplateRequestDTO request = new CreateTemplateRequestDTO(
                "reservation-confirmed",
                Channel.EMAIL,
                "Subject",
                "Body"
        );

        when(templateMapper.toEntity(request)).thenReturn(template);
        when(templateRepository.save(template)).thenReturn(Mono.just(template));
        when(templateMapper.toDTO(template)).thenReturn(responseDTO);

        StepVerifier.create(templateService.create(request))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void update_ShouldUpdate_WhenExists() {
        UpdateTemplateRequestDTO request = new UpdateTemplateRequestDTO(
                "updated-code",
                Channel.EMAIL,
                "New Subject",
                "New Body"
        );

        when(templateRepository.findById(1L)).thenReturn(Mono.just(template));
        when(templateRepository.save(any(Template.class))).thenReturn(Mono.just(template));
        when(templateMapper.toDTO(template)).thenReturn(responseDTO);

        StepVerifier.create(templateService.update(1L, request))
                .expectNext(responseDTO)
                .verifyComplete();

        verify(templateRepository).findById(1L);
        verify(templateRepository).save(any());
    }

    @Test
    void update_ShouldThrowException_WhenNotExists() {
        UpdateTemplateRequestDTO request = new UpdateTemplateRequestDTO(
                null, null, null, null
        );

        when(templateRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(templateService.update(1L, request))
                .expectError(TemplateNotFoundException.class)
                .verify();
    }

    @Test
    void delete_ShouldDelete_WhenExists() {
        when(templateRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(templateRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(templateService.delete(1L))
                .verifyComplete();

        verify(templateRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        when(templateRepository.existsById(1L)).thenReturn(Mono.just(false));

        StepVerifier.create(templateService.delete(1L))
                .expectError(TemplateNotFoundException.class)
                .verify();
    }
}