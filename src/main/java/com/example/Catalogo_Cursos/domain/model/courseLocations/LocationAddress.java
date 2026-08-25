package com.example.Catalogo_Cursos.domain.model.courseLocations;

import java.util.Objects;

public record LocationAddress(
    String value) {

  public LocationAddress {

    Objects.requireNonNull(
        value,
        "Location address cannot be null");

    if (value.isBlank()) {

      throw new IllegalArgumentException(
          "Location address cannot be blank");
    }

    if (value.length() > 250) {

      throw new IllegalArgumentException(
          "Location address cannot exceed 250 characters");
    }
  }

  public static LocationAddress of(
      String value) {

    return new LocationAddress(
        value.trim());
  }
}
