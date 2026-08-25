package com.example.Catalogo_Cursos.domain.model.shared.exception;

public class DomainException
    extends RuntimeException {

  public DomainException(
      String message) {
    super(message);
  }
}
