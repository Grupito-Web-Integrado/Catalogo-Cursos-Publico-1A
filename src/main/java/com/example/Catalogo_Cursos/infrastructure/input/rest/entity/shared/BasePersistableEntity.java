package com.example.Catalogo_Cursos.infrastructure.input.rest.entity.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BasePersistableEntity<ID>
    implements Persistable<ID> {

  @Transient
  @lombok.Builder.Default
  private boolean isNew = true;

  @Override
  public boolean isNew() {
    return isNew;
  }

  public void markNotNew() {
    this.isNew = false;
  }

  public void markAsNew() {
    this.isNew = true;
  }
}
