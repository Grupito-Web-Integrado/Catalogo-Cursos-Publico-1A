package com.example.Catalogo_Cursos.application.command.courseCategory;

import com.example.Catalogo_Cursos.application.shared.command.Command;

import java.util.UUID;

public record UpdateCategoryDescriptionCommand(

    UUID categoryId,
    String description

) implements Command {
}
