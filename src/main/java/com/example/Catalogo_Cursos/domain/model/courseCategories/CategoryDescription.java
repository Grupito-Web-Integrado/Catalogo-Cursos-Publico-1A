package com.example.Catalogo_Cursos.domain.model.courseCategories;

import java.util.Objects;

public record CategoryDescription(
    String value) {

  public CategoryDescription {

    Objects.requireNonNull(
        value,
        "Category description cannot be null");

    if (value.isBlank()) {

      throw new IllegalArgumentException(
          "Category description cannot be blank");
    }
  }

  public static CategoryDescription of(
      String value) {

    return new CategoryDescription(
        value.trim());
  }
}
