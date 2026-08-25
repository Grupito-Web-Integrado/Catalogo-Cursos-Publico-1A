package com.example.Catalogo_Cursos.infrastructure.input.rest.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.shared.AuditablePersistableEntity;

import java.util.UUID;

@Table("course_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CourseCategoryEntity
    extends AuditablePersistableEntity<UUID> {

  @Id
  private UUID id;

  private String name;

  private String description;

  private String status;

  @Override
  public UUID getId() {
    return id;
  }
}
