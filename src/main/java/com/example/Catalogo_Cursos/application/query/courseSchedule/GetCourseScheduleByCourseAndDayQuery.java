package com.example.Catalogo_Cursos.application.query.courseSchedule;

import com.example.Catalogo_Cursos.application.shared.query.Query;

import java.time.DayOfWeek;
import java.util.UUID;

public record GetCourseScheduleByCourseAndDayQuery(
    UUID courseId,
    DayOfWeek dayOfWeek,
    int page,
    int size) implements Query {
}
