package com.example.Catalogo_Cursos.application.query;
import com.example.Catalogo_Cursos.application.shared.query.Query;

public record GetCourseByCodeQuery(
        String code
) implements Query {
}
