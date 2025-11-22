package com.unimag.notificationservice.repository;

import com.unimag.notificationservice.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    Optional<Template> findByCode(String code);
}
