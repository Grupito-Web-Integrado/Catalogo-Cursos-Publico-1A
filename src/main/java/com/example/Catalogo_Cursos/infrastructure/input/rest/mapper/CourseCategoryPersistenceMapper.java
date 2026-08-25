package com.example.Catalogo_Cursos.infrastructure.input.rest.mapper;

import com.example.Catalogo_Cursos.domain.model.courseCategories.CategoryDescription;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CategoryName;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CategoryStatus;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategory;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategoryId;
import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.CourseCategoryEntity;

import org.springframework.stereotype.Component;

@Component
public class CourseCategoryPersistenceMapper {

  // =========================================================
  // ENTITY -> DOMAIN
  // =========================================================

  public CourseCategory toDomain(
      CourseCategoryEntity entity) {

    if (entity == null) {
      return null;
    }

    return CourseCategory.rehydrate(

        CourseCategoryId.of(
            entity.getId()),

        CategoryName.of(
            entity.getName()),

        entity.getDescription() == null
            ? null
            : CategoryDescription.of(
                entity.getDescription()),

        CategoryStatus.valueOf(
            entity.getStatus()),

        entity.getCreatedAt(),

        entity.getUpdatedAt(),

        entity.getVersion(),

        entity.getDeleted());
  }

  // =========================================================
  // DOMAIN -> ENTITY
  // =========================================================

  public CourseCategoryEntity toEntity(
      CourseCategory category) {

    if (category == null) {
      return null;
    }

    CourseCategoryEntity entity = CourseCategoryEntity.builder()

        .id(
            category.getId().value())

        .name(
            category.getName().value())

        .description(
            category.getDescription() != null
                ? category.getDescription().value()
                : null)

        .status(
            category.getStatus().name())

        .createdAt(
            category.getCreatedAt())

        .updatedAt(
            category.getUpdatedAt())

        .version(
            category.getVersion())

        .deleted(
            category.isDeleted())

        .build();

    // =========================================================
    // PERSISTENCE STATE
    // =========================================================

    if (category.isNew()) {
      entity.markAsNew();
    } else {
      entity.markNotNew();
    }

    return entity;
  }
}
