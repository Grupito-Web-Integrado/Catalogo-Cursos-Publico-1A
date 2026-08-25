package com.example.Catalogo_Cursos.domain.model.course;

import java.util.Objects;
import java.util.UUID;

public record CourseId(UUID value) {

  public CourseId {

    Objects.requireNonNull(
        value,
        "CourseId cannot be null");
  }

  public static CourseId generate() {

    return new CourseId(
        UUID.randomUUID());
  }

  public static CourseId of(UUID value) {

    return new CourseId(value);
  }
}
