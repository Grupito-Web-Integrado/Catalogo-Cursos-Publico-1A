package com.example.Catalogo_Cursos.application.shared.exception;

public abstract class ApplicationException
    extends RuntimeException {

  protected ApplicationException(
      String message) {
    super(message);
  }

  protected ApplicationException(
      String message,
      Throwable cause) {
    super(
        message,
        cause);
  }
}
