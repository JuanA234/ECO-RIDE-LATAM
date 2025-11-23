package com.unimag.paymentservice.entity;

import com.unimag.paymentservice.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table(name = "payment_intents")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntent {

    @Id
    private Long id;

    private Long reservationId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
}
