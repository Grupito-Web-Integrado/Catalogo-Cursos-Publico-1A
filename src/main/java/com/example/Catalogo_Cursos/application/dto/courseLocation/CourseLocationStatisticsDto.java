package com.example.Catalogo_Cursos.application.dto.courseLocation;

public record CourseLocationStatisticsDto(

    long total,

    long locationsWithCourses,

    long totalCapacity

) {
}
