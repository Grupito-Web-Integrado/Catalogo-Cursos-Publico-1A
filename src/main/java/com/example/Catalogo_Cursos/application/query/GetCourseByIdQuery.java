package com.example.Catalogo_Cursos.application.query;
import com.example.Catalogo_Cursos.application.shared.query.Query;

import java.util.UUID;

public record GetCourseByIdQuery(
        UUID courseId
) implements Query {
}
