package com.example.Catalogo_Cursos.application.query.courseLocation;

import com.example.Catalogo_Cursos.application.shared.query.Query;

public record SearchCourseLocationsQuery(
    String text,
    int page,
    int size) implements Query {
}
