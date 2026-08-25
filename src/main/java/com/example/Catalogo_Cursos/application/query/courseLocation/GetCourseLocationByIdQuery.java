package com.example.Catalogo_Cursos.application.query.courseLocation;

import com.example.Catalogo_Cursos.application.shared.query.Query;

import java.util.UUID;

public record GetCourseLocationByIdQuery(
    UUID courseLocationId) implements Query {
}
