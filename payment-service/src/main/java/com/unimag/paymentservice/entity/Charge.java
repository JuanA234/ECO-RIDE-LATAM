package com.unimag.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "charges")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Charge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long paymentIntentId;
    private String provider;
    private String providerRef;
    private LocalDateTime createdAt;

}
