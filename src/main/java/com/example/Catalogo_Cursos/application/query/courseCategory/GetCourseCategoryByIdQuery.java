package com.example.Catalogo_Cursos.application.query.courseCategory;

import com.example.Catalogo_Cursos.application.shared.query.Query;

import java.util.UUID;

public record GetCourseCategoryByIdQuery(
    UUID categoryId) implements Query {
}
