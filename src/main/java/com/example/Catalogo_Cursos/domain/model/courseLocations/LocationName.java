package com.example.Catalogo_Cursos.domain.model.courseLocations;

import java.util.Objects;

public record LocationName(
    String value) {

  public LocationName {

    Objects.requireNonNull(
        value,
        "Location name cannot be null");

    if (value.isBlank()) {

      throw new IllegalArgumentException(
          "Location name cannot be blank");
    }

    if (value.length() > 150) {

      throw new IllegalArgumentException(
          "Location name cannot exceed 150 characters");
    }
  }

  public static LocationName of(
      String value) {

    return new LocationName(
        value.trim());
  }
}
