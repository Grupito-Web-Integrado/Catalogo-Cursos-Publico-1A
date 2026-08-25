package com.example.Catalogo_Cursos.application.dto.course;

import java.util.UUID;

public record CourseSuggestionDto(

    UUID id,

    String code,

    String name

) {
}
