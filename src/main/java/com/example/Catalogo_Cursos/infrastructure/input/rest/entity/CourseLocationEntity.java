package com.example.Catalogo_Cursos.infrastructure.input.rest.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.shared.AuditablePersistableEntity;

import java.time.Instant;
import java.util.UUID;

@Table("course_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CourseLocationEntity extends AuditablePersistableEntity<UUID> {

  @Id
  private UUID id;

  private UUID courseId;

  private String name;

  private String address;

  private String city;

  private String reference;

  private Integer capacity;

  @Override
  public UUID getId() {
    return id;
  }
}
