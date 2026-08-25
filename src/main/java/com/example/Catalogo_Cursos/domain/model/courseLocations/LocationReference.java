package com.example.Catalogo_Cursos.domain.model.courseLocations;

import java.util.Objects;

public record LocationReference(
    String value) {

  public LocationReference {

    Objects.requireNonNull(
        value,
        "Location reference cannot be null");

    if (value.isBlank()) {

      throw new IllegalArgumentException(
          "Location reference cannot be blank");
    }

    if (value.length() > 250) {

      throw new IllegalArgumentException(
          "Location reference cannot exceed 250 characters");
    }
  }

  public static LocationReference of(
      String value) {

    return new LocationReference(
        value.trim());
  }
}
