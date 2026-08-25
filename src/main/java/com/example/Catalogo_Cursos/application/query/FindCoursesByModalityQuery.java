package com.example.Catalogo_Cursos.application.query;
import com.example.Catalogo_Cursos.application.shared.query.Query;

public record FindCoursesByModalityQuery(
        String modality,
        int page,
        int size
) implements Query {
}
