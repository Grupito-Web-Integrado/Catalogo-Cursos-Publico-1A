package com.example.Catalogo_Cursos.domain.model.course;

public record CourseCapacity(Integer value) {

  public CourseCapacity {

    if (value == null || value <= 0) {
      throw new IllegalArgumentException(
          "Course capacity must be greater than zero");
    }
  }

  public static CourseCapacity of(Integer value) {
    return new CourseCapacity(value);
  }
}
