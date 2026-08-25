package com.example.Catalogo_Cursos.application.query;
import com.example.Catalogo_Cursos.application.shared.query.Query;

import java.time.LocalDate;

public record FindCoursesByDateRangeQuery(
        LocalDate from,
        LocalDate to,
        int page,
        int size
) implements Query {
}
