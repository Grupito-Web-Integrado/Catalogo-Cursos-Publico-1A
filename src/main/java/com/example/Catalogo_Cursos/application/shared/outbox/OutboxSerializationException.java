package com.example.Catalogo_Cursos.application.shared.outbox;

public class OutboxSerializationException extends RuntimeException {
  public OutboxSerializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
