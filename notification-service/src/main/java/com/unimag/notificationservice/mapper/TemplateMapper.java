package com.unimag.notificationservice.mapper;

import com.unimag.notificationservice.dto.request.CreateTemplateRequestDTO;
import com.unimag.notificationservice.dto.response.TemplateResponseDTO;
import com.unimag.notificationservice.entity.Template;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TemplateMapper {

    TemplateResponseDTO toDTO(Template template);

    @Mapping(target = "id", ignore = true)
    Template toEntity(CreateTemplateRequestDTO request);
}
