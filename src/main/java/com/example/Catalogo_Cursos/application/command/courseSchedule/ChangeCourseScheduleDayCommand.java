package com.example.Catalogo_Cursos.application.command.courseSchedule;

import com.example.Catalogo_Cursos.application.shared.command.Command;

import java.time.DayOfWeek;
import java.util.UUID;

public record ChangeCourseScheduleDayCommand(

    UUID scheduleId,
    DayOfWeek dayOfWeek

) implements Command {
}
