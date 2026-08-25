package com.example.Catalogo_Cursos.application.dto.courseSchedule;

public record CourseScheduleStatisticsDto(

    long total,

    long coursesWithSchedule,

    long totalScheduledHours

) {
}
