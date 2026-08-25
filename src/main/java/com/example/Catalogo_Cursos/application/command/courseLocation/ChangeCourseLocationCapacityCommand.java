package com.example.Catalogo_Cursos.application.command.courseLocation;

import com.example.Catalogo_Cursos.application.shared.command.Command;

import java.util.UUID;

public record ChangeCourseLocationCapacityCommand(

    UUID locationId,
    Integer capacity

) implements Command {
}
