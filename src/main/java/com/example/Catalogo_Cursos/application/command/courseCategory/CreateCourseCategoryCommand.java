package com.example.Catalogo_Cursos.application.command.courseCategory;

import com.example.Catalogo_Cursos.application.shared.command.Command;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CategoryStatus;

public record CreateCourseCategoryCommand(

    String name,
    String description,
    CategoryStatus status

) implements Command {
}
