package com.example.Catalogo_Cursos.domain.model.course;

public record CourseCode(String value) {

  public CourseCode {

    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(
          "Course code cannot be blank");
    }
  }

  public static CourseCode of(String value) {
    return new CourseCode(
        value.trim().toUpperCase());
  }
}
