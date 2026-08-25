package com.example.Catalogo_Cursos.application.dto.courseSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record CourseScheduleDto(

    UUID id,

    UUID courseId,

    DayOfWeek dayOfWeek,

    LocalTime startTime,

    LocalTime endTime,

    String room

) {
}
