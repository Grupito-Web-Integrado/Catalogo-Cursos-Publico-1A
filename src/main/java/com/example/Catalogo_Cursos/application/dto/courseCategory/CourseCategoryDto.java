package com.example.Catalogo_Cursos.application.dto.courseCategory;

import java.util.UUID;

public record CourseCategoryDto(

    UUID id,

    String name,

    String description,

    String status

) {
}
