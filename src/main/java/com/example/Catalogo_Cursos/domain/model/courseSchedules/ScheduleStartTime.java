package com.example.Catalogo_Cursos.domain.model.courseSchedules;

import java.time.LocalTime;
import java.util.Objects;

public record ScheduleStartTime(LocalTime value) {

  public ScheduleStartTime {

    Objects.requireNonNull(
        value,
        "Schedule start time cannot be null");
  }

  public static ScheduleStartTime of(
      LocalTime value) {

    return new ScheduleStartTime(value);
  }
}
