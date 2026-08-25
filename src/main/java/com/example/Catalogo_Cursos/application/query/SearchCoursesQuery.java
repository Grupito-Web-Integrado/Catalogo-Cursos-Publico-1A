package com.example.Catalogo_Cursos.application.query;
import com.example.Catalogo_Cursos.application.shared.query.Query;

public record SearchCoursesQuery(
        String text,
        int page,
        int size
) implements Query {
}
