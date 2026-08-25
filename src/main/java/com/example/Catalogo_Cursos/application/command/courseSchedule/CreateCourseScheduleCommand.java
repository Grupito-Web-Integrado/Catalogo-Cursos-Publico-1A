package com.example.Catalogo_Cursos.application.command.courseSchedule;

import com.example.Catalogo_Cursos.application.shared.command.Command;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record CreateCourseScheduleCommand(

    UUID courseId,
    DayOfWeek dayOfWeek,
    LocalTime startTime,
    LocalTime endTime,
    String room

) implements Command {
}
