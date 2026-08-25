package com.example.Catalogo_Cursos.application.dto.courseLocation;

import java.util.UUID;

public record CourseLocationSummaryDto(

    UUID id,

    UUID courseId,

    String name,

    String city,

    Integer capacity

) {
}
