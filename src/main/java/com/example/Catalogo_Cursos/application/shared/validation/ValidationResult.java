package com.example.Catalogo_Cursos.application.shared.validation;

import java.util.List;

public record ValidationResult(
    boolean valid,
    List<String> errors) {
}
