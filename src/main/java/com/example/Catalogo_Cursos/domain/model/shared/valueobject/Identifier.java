package com.example.Catalogo_Cursos.domain.model.shared.valueobject;

import java.util.UUID;

public abstract class Identifier {

  protected final UUID value;

  protected Identifier(UUID value) {

    if (value == null) {
      throw new IllegalArgumentException(
          "Identifier cannot be null");
    }

    this.value = value;
  }

  public UUID value() {
    return value;
  }
}
