package com.example.Catalogo_Cursos.domain.model.course;

public record CourseName(String value) {

  public CourseName {

    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(
          "Course name cannot be blank");
    }

    if (value.length() > 200) {
      throw new IllegalArgumentException(
          "Course name cannot exceed 200 characters");
    }
  }

  public static CourseName of(String value) {
    return new CourseName(value.trim());
  }
}
