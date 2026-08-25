package com.example.Catalogo_Cursos.domain.model.shared.valueobject;

import java.net.URI;

public record Url(String value) {

  public Url {

    try {
      URI.create(value);
    } catch (Exception e) {

      throw new IllegalArgumentException(
          "Invalid URL");
    }
  }
}
