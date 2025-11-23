package com.unimag.notificationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "outbox")
public class Outbox {

    @Id
    private Long id;
    private String eventType;
    private String payload;
    private OutboxStatus status;
    private Integer retries;
}
