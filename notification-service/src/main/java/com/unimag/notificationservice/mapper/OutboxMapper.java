package com.unimag.notificationservice.mapper;

import com.unimag.notificationservice.dto.response.OutboxResponseDTO;
import com.unimag.notificationservice.entity.Outbox;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OutboxMapper {

    OutboxResponseDTO toDTO(Outbox outbox);

}
