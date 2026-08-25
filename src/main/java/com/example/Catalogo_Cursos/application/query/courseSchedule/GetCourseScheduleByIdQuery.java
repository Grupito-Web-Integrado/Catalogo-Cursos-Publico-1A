package com.example.Catalogo_Cursos.application.query.courseSchedule;

import com.example.Catalogo_Cursos.application.shared.query.Query;

import java.util.UUID;

public record GetCourseScheduleByIdQuery(
    UUID courseScheduleId) implements Query {
}
