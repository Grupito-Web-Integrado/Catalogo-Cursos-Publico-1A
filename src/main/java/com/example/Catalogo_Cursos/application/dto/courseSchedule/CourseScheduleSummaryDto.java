package com.example.Catalogo_Cursos.application.dto.courseSchedule;

import java.time.LocalTime;
import java.util.UUID;

public record CourseScheduleSummaryDto(

    UUID id,

    UUID courseId,

    String dayOfWeek,

    LocalTime startTime,

    LocalTime endTime,

    String room

) {
}
