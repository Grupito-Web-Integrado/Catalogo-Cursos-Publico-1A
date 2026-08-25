package com.example.Catalogo_Cursos.domain.model.shared.aggregate;

import java.time.Instant;

public abstract class AuditableAggregateRoot<ID>
    extends AggregateRoot<ID> {

  protected Instant createdAt;
  protected Instant updatedAt;
  protected Long version;
  protected boolean deleted;

  /**
   * Indica si la entidad ya fue persistida.
   *
   * false = entidad nueva
   * true = entidad ya persistida
   */
  protected boolean persisted;

  protected AuditableAggregateRoot() {
    this.persisted = false;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Long getVersion() {
    return version;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public boolean isPersisted() {
    return persisted;
  }

  public boolean isNew() {
    return !persisted;
  }

  protected void markAsCreated() {
    Instant now = Instant.now();

    this.createdAt = now;
    this.updatedAt = now;
    this.persisted = false;
  }

  /**
   * Marca la entidad como persistida.
   */
  public void markAsPersisted() {
    this.persisted = true;
  }

  public void markAsDeleted() {
    this.deleted = true;
    touch();
  }

  protected void touch() {
    this.updatedAt = Instant.now();
  }
}
