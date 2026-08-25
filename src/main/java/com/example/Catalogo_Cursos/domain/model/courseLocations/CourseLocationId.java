package com.example.Catalogo_Cursos.domain.model.courseLocations;

import java.util.Objects;
import java.util.UUID;

public record CourseLocationId(UUID value) {

  public CourseLocationId {

    Objects.requireNonNull(
        value,
        "CourseLocationId cannot be null");
  }

  public static CourseLocationId generate() {

    return new CourseLocationId(
        UUID.randomUUID());
  }

  public static CourseLocationId of(
      UUID value) {

    return new CourseLocationId(
        value);
  }
}
