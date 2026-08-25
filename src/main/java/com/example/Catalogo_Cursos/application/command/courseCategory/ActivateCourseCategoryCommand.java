package com.example.Catalogo_Cursos.application.command.courseCategory;

import com.example.Catalogo_Cursos.application.shared.command.Command;

import java.util.UUID;

public record ActivateCourseCategoryCommand(
    UUID courseCategoryId) implements Command {
}
