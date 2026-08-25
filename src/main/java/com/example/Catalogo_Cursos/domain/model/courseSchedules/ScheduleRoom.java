package com.example.Catalogo_Cursos.domain.model.courseSchedules;

import java.util.Objects;

public record ScheduleRoom(String value) {

  public ScheduleRoom {

    Objects.requireNonNull(
        value,
        "Schedule room cannot be null");

    if (value.isBlank()) {

      throw new IllegalArgumentException(
          "Schedule room cannot be blank");
    }

    if (value.length() > 100) {

      throw new IllegalArgumentException(
          "Schedule room cannot exceed 100 characters");
    }
  }

  public static ScheduleRoom of(String value) {

    return new ScheduleRoom(
        value.trim());
  }
}
