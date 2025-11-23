package com.unimag.passengerservice.entity;

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
@Table(name = "ratings")
public class Rating {

    @Id
    private Long id;
    private Long tripId;
    private Long fromId;
    private Long toId;
    private Integer score;
    private String comment;
}
