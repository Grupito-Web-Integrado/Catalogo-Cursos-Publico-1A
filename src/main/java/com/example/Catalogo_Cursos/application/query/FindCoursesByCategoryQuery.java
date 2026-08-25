package com.example.Catalogo_Cursos.application.query;
import com.example.Catalogo_Cursos.application.shared.query.Query;

import java.util.UUID;

public record FindCoursesByCategoryQuery(
        UUID categoryId,
        int page,
        int size
) implements Query {
}
