package com.unimag.paymentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Table(name = "refunds")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Refund {
    @Id
    private Long id;

    private Long chargeId;

    private BigDecimal amount;
    private String reason;
    private LocalDateTime createdAt;
}
