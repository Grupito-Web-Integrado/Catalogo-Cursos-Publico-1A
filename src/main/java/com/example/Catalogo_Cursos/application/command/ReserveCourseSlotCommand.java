package com.example.Catalogo_Cursos.application.command;

import com.example.Catalogo_Cursos.application.shared.command.Command;

import java.util.UUID;

public record ReserveCourseSlotCommand(

    UUID courseId

) implements Command {
}
