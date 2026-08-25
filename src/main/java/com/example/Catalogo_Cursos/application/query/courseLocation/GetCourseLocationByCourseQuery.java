package com.example.Catalogo_Cursos.application.query.courseLocation;

import com.example.Catalogo_Cursos.application.shared.query.Query;

import java.util.UUID;

public record GetCourseLocationByCourseQuery(
    UUID courseId,
    int page,
    int size) implements Query {
}
