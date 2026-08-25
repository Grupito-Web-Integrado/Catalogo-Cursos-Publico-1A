package com.example.Catalogo_Cursos.application.command.courseCategory;

import java.util.UUID;

import com.example.Catalogo_Cursos.application.shared.command.Command;

public record DeactivateCourseCategoryCommand(
        UUID courseCategoryId
) implements Command {} 
