package com.example.Catalogo_Cursos.domain.model.courseCategories;

import java.util.Objects;

public record CategoryName(
    String value) {

  public CategoryName {

    Objects.requireNonNull(
        value,
        "Category name cannot be null");

    if (value.isBlank()) {

      throw new IllegalArgumentException(
          "Category name cannot be blank");
    }

    if (value.length() > 100) {

      throw new IllegalArgumentException(
          "Category name cannot exceed 100 characters");
    }
  }

  public static CategoryName of(
      String value) {

    return new CategoryName(
        value.trim());
  }
}
