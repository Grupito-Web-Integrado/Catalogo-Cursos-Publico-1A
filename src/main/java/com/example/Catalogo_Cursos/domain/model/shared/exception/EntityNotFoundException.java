package com.example.Catalogo_Cursos.domain.model.shared.exception;

public class EntityNotFoundException
    extends DomainException {

  public EntityNotFoundException(
      String message) {
    super(message);
  }
}
