package com.example.Catalogo_Cursos.domain.model.courseCategories;

import java.util.Objects;
import java.util.UUID;

public record CourseCategoryId(
    UUID value) {

  public CourseCategoryId {

    Objects.requireNonNull(
        value,
        "CourseCategoryId cannot be null");
  }

  public static CourseCategoryId generate() {

    return new CourseCategoryId(
        UUID.randomUUID());
  }

  public static CourseCategoryId of(
      UUID value) {

    return new CourseCategoryId(
        value);
  }
}
