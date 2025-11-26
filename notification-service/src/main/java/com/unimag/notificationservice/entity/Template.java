package com.unimag.notificationservice.entity;

import com.unimag.notificationservice.entity.enums.Channel;
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
@Table(name = "templates")
public class Template {

    @Id
    private Long id;
    private String code;
    private Channel channel;
    private String subject;
    private String body;
}
