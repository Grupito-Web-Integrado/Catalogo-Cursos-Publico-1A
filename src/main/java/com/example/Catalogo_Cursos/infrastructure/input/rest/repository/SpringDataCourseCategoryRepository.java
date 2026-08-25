package com.example.Catalogo_Cursos.infrastructure.input.rest.repository;

import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.CourseCategoryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface SpringDataCourseCategoryRepository
    extends ReactiveCrudRepository<CourseCategoryEntity, UUID> {
}
