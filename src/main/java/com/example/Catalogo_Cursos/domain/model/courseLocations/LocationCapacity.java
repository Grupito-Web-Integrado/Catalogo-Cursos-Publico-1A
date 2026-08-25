package com.example.Catalogo_Cursos.domain.model.courseLocations;

import java.util.Objects;

public record LocationCapacity(
    Integer value) {

  public LocationCapacity {

    Objects.requireNonNull(
        value,
        "Location capacity cannot be null");

    if (value <= 0) {

      throw new IllegalArgumentException(
          "Location capacity must be greater than zero");
    }
  }

  public static LocationCapacity of(
      Integer value) {

    return new LocationCapacity(
        value);
  }
}
