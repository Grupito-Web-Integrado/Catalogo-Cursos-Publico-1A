package com.example.Catalogo_Cursos.application.query.courseSchedule;

import com.example.Catalogo_Cursos.application.shared.query.Query;

import java.time.DayOfWeek;

public record GetCourseSchedulesByDayQuery(
    DayOfWeek dayOfWeek,
    int page,
    int size) implements Query {
}
