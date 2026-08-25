package com.example.Catalogo_Cursos.domain.model.courseSchedules;

import java.time.LocalTime;
import java.util.Objects;

public record ScheduleEndTime(LocalTime value) {

  public ScheduleEndTime {

    Objects.requireNonNull(
        value,
        "Schedule end time cannot be null");
  }

  public static ScheduleEndTime of(
      LocalTime value) {

    return new ScheduleEndTime(value);
  }
}
