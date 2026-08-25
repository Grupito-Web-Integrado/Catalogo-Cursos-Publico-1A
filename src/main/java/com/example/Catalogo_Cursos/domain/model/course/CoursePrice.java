package com.example.Catalogo_Cursos.domain.model.course;

import java.math.BigDecimal;
import java.util.Objects;

public record CoursePrice(
    BigDecimal amount,
    String currency) {

  public CoursePrice {

    Objects.requireNonNull(amount);
    Objects.requireNonNull(currency);

    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException(
          "Course price cannot be negative");
    }

    if (currency.isBlank()) {
      throw new IllegalArgumentException(
          "Currency cannot be blank");
    }
  }

  public static CoursePrice of(
      BigDecimal amount,
      String currency) {

    return new CoursePrice(
        amount,
        currency.trim().toUpperCase());
  }
}
