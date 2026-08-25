package com.example.Catalogo_Cursos.application.dto.courseLocation;

import java.util.UUID;

public record CourseLocationSuggestionDto(

    UUID id,

    String name,

    String city

) {
}
