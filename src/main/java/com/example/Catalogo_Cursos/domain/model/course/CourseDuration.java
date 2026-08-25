package com.example.Catalogo_Cursos.domain.model.course;

public record CourseDuration(Integer value) {

  public CourseDuration {

    if (value == null || value <= 0) {
      throw new IllegalArgumentException(
          "Course duration must be greater than zero");
    }
  }

  public static CourseDuration of(Integer value) {
    return new CourseDuration(value);
  }
}
