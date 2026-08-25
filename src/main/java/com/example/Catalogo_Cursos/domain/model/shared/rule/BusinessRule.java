package com.example.Catalogo_Cursos.domain.model.shared.rule;

public interface BusinessRule {

  boolean isBroken();

  String message();
}
