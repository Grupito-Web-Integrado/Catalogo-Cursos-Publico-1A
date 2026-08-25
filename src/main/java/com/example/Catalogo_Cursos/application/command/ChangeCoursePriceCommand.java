package com.example.Catalogo_Cursos.application.command;

import com.example.Catalogo_Cursos.application.shared.command.Command;

import java.math.BigDecimal;
import java.util.UUID;

public record ChangeCoursePriceCommand(

    UUID courseId,
    BigDecimal amount,
    String currency

) implements Command {
}
