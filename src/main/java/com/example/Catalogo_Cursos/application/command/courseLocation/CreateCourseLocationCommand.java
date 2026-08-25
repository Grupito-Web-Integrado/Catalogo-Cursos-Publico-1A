package com.example.Catalogo_Cursos.application.command.courseLocation;

import com.example.Catalogo_Cursos.application.shared.command.Command;

import java.util.UUID;

public record CreateCourseLocationCommand(

    UUID courseId,
    String name,
    String address,
    String city,
    String reference,
    Integer capacity

) implements Command {
}
