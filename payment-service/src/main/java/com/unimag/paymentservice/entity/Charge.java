package com.unimag.paymentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "charges")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Charge {

    @Id
    private Long id;

    private Long paymentIntentId;
    private String provider;
    private String providerRef;
    private LocalDateTime createdAt;

}
