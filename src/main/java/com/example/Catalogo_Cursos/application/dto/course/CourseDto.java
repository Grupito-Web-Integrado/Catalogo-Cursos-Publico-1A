package com.example.Catalogo_Cursos.application.dto.course;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationDto;
import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleDto;

public record CourseDto(

    UUID id,

    String code,

    String name,

    String description,

    String modality,

    BigDecimal price,

    String currency,

    LocalDate startDate,

    LocalDate endDate,

    LocalTime startTime,

    Integer durationHours,

    Integer capacity,

    Integer availableSlots,

    String status,

    List<CourseScheduleDto> schedules,

    List<CourseLocationDto> locations,

    List<CourseCategoryDto> categories,

    java.time.Instant createdAt,

    java.time.Instant updatedAt

) {
}
