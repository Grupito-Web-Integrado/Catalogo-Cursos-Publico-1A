package com.example.Catalogo_Cursos.application.query;
import com.example.Catalogo_Cursos.application.shared.query.Query;

public record FindCoursesOrderedByNameQuery(
        boolean ascending,
        int page,
        int size
) implements Query {
}
