package com.example.Catalogo_Cursos.domain.model.courseLocations;

import java.util.Objects;

public record LocationCity(
    String value) {

  public LocationCity {

    Objects.requireNonNull(
        value,
        "Location city cannot be null");

    if (value.isBlank()) {

      throw new IllegalArgumentException(
          "Location city cannot be blank");
    }

    if (value.length() > 100) {

      throw new IllegalArgumentException(
          "Location city cannot exceed 100 characters");
    }
  }

  public static LocationCity of(
      String value) {

    return new LocationCity(
        value.trim());
  }
}
