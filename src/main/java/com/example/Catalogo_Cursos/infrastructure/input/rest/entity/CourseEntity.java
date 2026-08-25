package com.example.Catalogo_Cursos.infrastructure.input.rest.entity;

import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.shared.AuditablePersistableEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Table("courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CourseEntity
    extends AuditablePersistableEntity<UUID> {

  @Id
  private UUID id;

  private String code;

  private String name;

  private String description;

  private String modality;

  private BigDecimal price;

  private String currency;

  private LocalDate startDate;

  private LocalDate endDate;

  private LocalTime startTime;

  private Integer durationHours;

  private Integer capacity;

  private Integer availableSlots;

  /**
   * Una sola categoría por curso.
   */
  private UUID categoryId;

  private String status;

  @Override
  public UUID getId() {
    return id;
  }
}
