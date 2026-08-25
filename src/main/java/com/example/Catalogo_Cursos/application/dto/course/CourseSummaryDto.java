package com.example.Catalogo_Cursos.application.dto.course;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CourseSummaryDto(

    UUID id,

    String code,

    String name,

    String modality,

    BigDecimal price,

    String currency,

    LocalDate startDate,

    LocalDate endDate,

    LocalTime startTime,

    Integer durationHours,

    Integer capacity,

    Integer availableSlots,

    String status

) {
}
