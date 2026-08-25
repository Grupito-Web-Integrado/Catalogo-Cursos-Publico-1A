package com.example.Catalogo_Cursos.application.dto.courseCategory;

import java.util.UUID;

public record CourseCategorySummaryDto(

    UUID id,

    String name,

    String status

) {
}
