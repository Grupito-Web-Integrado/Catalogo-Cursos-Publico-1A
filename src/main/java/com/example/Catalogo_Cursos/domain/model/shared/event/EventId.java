package com.example.Catalogo_Cursos.domain.model.shared.event;

import java.util.UUID;

public record EventId(UUID value) {

  public EventId {
    if (value == null) {
      throw new IllegalArgumentException(
          "EventId cannot be null");
    }
  }

  public static EventId generate() {
    return new EventId(UUID.randomUUID());
  }

}
