package com.example.Catalogo_Cursos.application.dto.course;

import java.math.BigDecimal;

public record CourseStatisticsDto(

    long totalCourses,

    long draftCourses,

    long publishedCourses,

    long cancelledCourses,

    long completedCourses,

    long availableCourses,

    long fullCourses,

    BigDecimal averagePrice,

    BigDecimal minimumPrice,

    BigDecimal maximumPrice

) {
}
