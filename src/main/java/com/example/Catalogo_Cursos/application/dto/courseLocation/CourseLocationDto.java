package com.example.Catalogo_Cursos.application.dto.courseLocation;

import java.util.UUID;

public record CourseLocationDto(

    UUID id,

    UUID courseId,

    String name,

    String address,

    String city,

    String reference,

    Integer capacity

) {
}
