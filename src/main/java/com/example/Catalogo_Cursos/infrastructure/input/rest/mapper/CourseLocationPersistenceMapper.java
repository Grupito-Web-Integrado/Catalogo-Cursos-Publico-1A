package com.example.Catalogo_Cursos.infrastructure.input.rest.mapper;

import com.example.Catalogo_Cursos.domain.model.course.CourseId;
import com.example.Catalogo_Cursos.domain.model.courseLocations.CourseLocation;
import com.example.Catalogo_Cursos.domain.model.courseLocations.CourseLocationId;
import com.example.Catalogo_Cursos.domain.model.courseLocations.LocationAddress;
import com.example.Catalogo_Cursos.domain.model.courseLocations.LocationCapacity;
import com.example.Catalogo_Cursos.domain.model.courseLocations.LocationCity;
import com.example.Catalogo_Cursos.domain.model.courseLocations.LocationName;
import com.example.Catalogo_Cursos.domain.model.courseLocations.LocationReference;
import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.CourseLocationEntity;

import org.springframework.stereotype.Component;

@Component
public class CourseLocationPersistenceMapper {

  // =========================================================
  // DOMAIN -> ENTITY
  // =========================================================

  public CourseLocationEntity toEntity(
      CourseLocation location) {

    if (location == null) {
      return null;
    }

    CourseLocationEntity entity = CourseLocationEntity.builder()

        .id(
            location.getId().value())

        .courseId(
            location.getCourseId().value())

        .name(
            location.getName().value())

        .address(
            location.getAddress().value())

        .city(
            location.getCity().value())

        .reference(
            location.getReference() != null
                ? location.getReference().value()
                : null)

        .capacity(
            location.getCapacity().value())

        .createdAt(
            location.getCreatedAt())

        .updatedAt(
            location.getUpdatedAt())

        .version(
            location.getVersion())

        .deleted(
            location.isDeleted())

        .build();

    // =========================================================
    // PERSISTENCE STATE
    // =========================================================

    /*
     * Spring Data R2DBC utiliza Persistable.isNew()
     * para decidir entre INSERT y UPDATE.
     *
     * Dominio nuevo -> INSERT
     * Dominio existente -> UPDATE
     */

    if (location.isNew()) {
      entity.markAsNew();
    } else {
      entity.markNotNew();
    }

    return entity;
  }

  // =========================================================
  // ENTITY -> DOMAIN
  // =========================================================

  public CourseLocation toDomain(
      CourseLocationEntity entity) {

    if (entity == null) {
      return null;
    }

    return CourseLocation.rehydrate(

        CourseLocationId.of(
            entity.getId()),

        CourseId.of(
            entity.getCourseId()),

        LocationName.of(
            entity.getName()),

        LocationAddress.of(
            entity.getAddress()),

        LocationCity.of(
            entity.getCity()),

        entity.getReference() == null
            ? null
            : LocationReference.of(
                entity.getReference()),

        LocationCapacity.of(
            entity.getCapacity()),

        entity.getCreatedAt(),

        entity.getUpdatedAt(),

        entity.getVersion(),

        entity.getDeleted());
  }
}
