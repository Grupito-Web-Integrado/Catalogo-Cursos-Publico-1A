package com.example.Catalogo_Cursos.domain.model.course;

public record CourseDescription(String value) {

  public CourseDescription {

    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(
          "Course description cannot be blank");
    }
  }

  public static CourseDescription of(String value) {
    return new CourseDescription(value.trim());
  }
}
