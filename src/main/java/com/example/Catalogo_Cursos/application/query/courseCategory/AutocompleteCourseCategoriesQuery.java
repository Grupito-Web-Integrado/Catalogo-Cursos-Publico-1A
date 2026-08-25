package com.example.Catalogo_Cursos.application.query.courseCategory;

import com.example.Catalogo_Cursos.application.shared.query.Query;

public record AutocompleteCourseCategoriesQuery(
    String text,
    int limit) implements Query {
}
