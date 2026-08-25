package com.example.Catalogo_Cursos.domain.model.courseSchedules;

import java.util.Objects;
import java.util.UUID;

public record CourseScheduleId(UUID value) {

  public CourseScheduleId {

    Objects.requireNonNull(
        value,
        "CourseScheduleId cannot be null");
  }

  public static CourseScheduleId generate() {

    return new CourseScheduleId(
        UUID.randomUUID());
  }

  public static CourseScheduleId of(UUID value) {

    return new CourseScheduleId(value);
  }
}
