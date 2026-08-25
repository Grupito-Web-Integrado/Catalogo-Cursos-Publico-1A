package com.example.Catalogo_Cursos.application.query.courseCategory;

import com.example.Catalogo_Cursos.application.shared.query.Query;

public record GetCourseCategoryByNameQuery(
    String name) implements Query {
}
