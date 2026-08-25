package com.example.Catalogo_Cursos.application.query;
import com.example.Catalogo_Cursos.application.shared.query.Query;

import java.time.LocalDate;

public record FindCoursesByCriteriaQuery(
        String name,
        String code,
        String modality,
        String status,
        LocalDate startDateFrom,
        LocalDate startDateTo,
        int page,
        int size
) implements Query {
}
