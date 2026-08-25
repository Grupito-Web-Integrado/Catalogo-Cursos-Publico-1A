package com.example.Catalogo_Cursos.domain.model.shared.exception;

public class BusinessRuleViolationException
    extends DomainException {

  public BusinessRuleViolationException(
      String message) {
    super(message);
  }
}
