package com.example.Catalogo_Cursos.application.command.courseSchedule;

import com.example.Catalogo_Cursos.application.shared.command.Command;

import java.util.UUID;

public record ChangeCourseScheduleRoomCommand(

    UUID scheduleId,
    String room

) implements Command {
}
