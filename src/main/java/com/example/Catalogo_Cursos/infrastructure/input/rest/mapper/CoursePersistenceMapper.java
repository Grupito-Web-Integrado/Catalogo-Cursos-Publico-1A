package com.example.Catalogo_Cursos.infrastructure.input.rest.mapper;

import com.example.Catalogo_Cursos.domain.model.course.AvailableSlots;
import com.example.Catalogo_Cursos.domain.model.course.Course;
import com.example.Catalogo_Cursos.domain.model.course.CourseCapacity;
import com.example.Catalogo_Cursos.domain.model.course.CourseCode;
import com.example.Catalogo_Cursos.domain.model.course.CourseDescription;
import com.example.Catalogo_Cursos.domain.model.course.CourseDuration;
import com.example.Catalogo_Cursos.domain.model.course.CourseId;
import com.example.Catalogo_Cursos.domain.model.course.CourseModality;
import com.example.Catalogo_Cursos.domain.model.course.CourseName;
import com.example.Catalogo_Cursos.domain.model.course.CoursePrice;
import com.example.Catalogo_Cursos.domain.model.course.CourseStatus;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategoryId;
import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.CourseEntity;

import org.springframework.stereotype.Component;

@Component
public class CoursePersistenceMapper {

  // =========================================================
  // DOMAIN -> ENTITY
  // =========================================================

  public CourseEntity toEntity(
      Course course) {

    if (course == null) {
      return null;
    }

    CourseEntity entity = CourseEntity.builder()

        .id(
            course.getId().value())

        .code(
            course.getCode().value())

        /*
         * CourseName actualmente expone String directamente.
         */
        .name(
            course.getName())

        /*
         * CourseDescription actualmente expone String directamente.
         */
        .description(
            course.getDescription())

        .modality(
            course.getModality().name())

        .price(
            course.getPrice().amount())

        .currency(
            course.getPrice().currency())

        .startDate(
            course.getStartDate())

        .endDate(
            course.getEndDate())

        .startTime(
            course.getStartTime())

        .durationHours(
            course.getDurationHours().value())

        .capacity(
            course.getCapacity().value())

        .availableSlots(
            course.getAvailableSlots().value())

        .categoryId(
            course.getCategoryId().value())

        .status(
            course.getStatus().name())

        .createdAt(
            course.getCreatedAt())

        .updatedAt(
            course.getUpdatedAt())

        .version(
            course.getVersion())

        .deleted(
            course.isDeleted())

        .build();

    // =========================================================
    // PERSISTENCE STATE
    // =========================================================

    /*
     * IMPORTANTE:
     *
     * Si el aggregate ya fue recuperado de BD:
     *
     * persisted = true
     * entity.isNew() = false
     * => UPDATE
     *
     * Si es un aggregate nuevo:
     *
     * persisted = false
     * entity.isNew() = true
     * => INSERT
     */
    if (course.isPersisted()) {
      entity.markNotNew();
    } else {
      entity.markAsNew();
    }

    return entity;
  }

  // =========================================================
  // ENTITY -> DOMAIN
  // =========================================================

  public Course toDomain(
      CourseEntity entity) {

    if (entity == null) {
      return null;
    }

    Course course = Course.rehydrate(

        CourseId.of(
            entity.getId()),

        CourseCode.of(
            entity.getCode()),

        CourseName.of(
            entity.getName()),

        entity.getDescription() == null
            ? null
            : CourseDescription.of(
                entity.getDescription()),

        CourseModality.valueOf(
            entity.getModality()),

        CoursePrice.of(
            entity.getPrice(),
            entity.getCurrency()),

        entity.getStartDate(),

        entity.getEndDate(),

        entity.getStartTime(),

        CourseDuration.of(
            entity.getDurationHours()),

        CourseCapacity.of(
            entity.getCapacity()),

        AvailableSlots.of(
            entity.getAvailableSlots()),

        CourseCategoryId.of(
            entity.getCategoryId()),

        CourseStatus.valueOf(
            entity.getStatus()),

        entity.getCreatedAt(),

        entity.getUpdatedAt(),

        entity.getVersion(),

        entity.getDeleted());

    // =========================================================
    // PERSISTENCE STATE
    // =========================================================

    /*
     * Esta instancia viene de la BD,
     * por lo tanto ya existe.
     */
    course.markAsPersisted();

    return course;
  }
}
