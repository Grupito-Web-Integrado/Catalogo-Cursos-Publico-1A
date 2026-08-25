package com.example.Catalogo_Cursos.domain.model.course;

public record AvailableSlots(Integer value) {

  public AvailableSlots {

    if (value == null || value < 0) {
      throw new IllegalArgumentException(
          "Available slots cannot be negative");
    }
  }

  public static AvailableSlots of(Integer value) {
    return new AvailableSlots(value);
  }
}
