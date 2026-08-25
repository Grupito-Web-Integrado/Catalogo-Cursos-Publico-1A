package com.example.Catalogo_Cursos.application.command.courseSchedule;

import com.example.Catalogo_Cursos.application.shared.command.Command;

import java.time.LocalTime;
import java.util.UUID;

public record ChangeCourseScheduleEndTimeCommand(

    UUID scheduleId,
    LocalTime endTime

) implements Command {
}
