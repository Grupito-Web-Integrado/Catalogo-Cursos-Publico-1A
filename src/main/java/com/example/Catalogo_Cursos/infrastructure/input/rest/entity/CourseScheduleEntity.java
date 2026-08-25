package com.example.Catalogo_Cursos.infrastructure.input.rest.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.shared.AuditablePersistableEntity;

import java.time.LocalTime;
import java.util.UUID;

@Table("course_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CourseScheduleEntity extends AuditablePersistableEntity<UUID> {

  @Id
  private UUID id;

  private UUID courseId;

  private String dayOfWeek;

  private LocalTime startTime;

  private LocalTime endTime;

  private String room;

  @Override
  public UUID getId() {
    return id;
  }
}
