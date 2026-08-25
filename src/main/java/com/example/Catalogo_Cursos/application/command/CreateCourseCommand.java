package com.example.Catalogo_Cursos.application.command;

import com.example.Catalogo_Cursos.application.shared.command.Command;
import com.example.Catalogo_Cursos.domain.model.course.CourseModality;
import com.example.Catalogo_Cursos.domain.model.course.CourseStatus;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategoryId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateCourseCommand(
    String code,
    String name,
    String description,
    CourseModality modality,
    BigDecimal price,
    String currency,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime startTime,
    Integer durationHours,
    Integer capacity,
    CourseCategoryId categoryId,
    CourseStatus status) implements Command {
}
