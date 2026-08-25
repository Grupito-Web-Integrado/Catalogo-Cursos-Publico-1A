package com.example.Catalogo_Cursos.infrastructure.input.rest.entity.shared.outbox;

import io.r2dbc.postgresql.codec.Json;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("outbox_events")
public record OutboxEventEntity(

    @Id UUID id,

    @Column("aggregatetype") String aggregateType,

    @Column("aggregateid") String aggregateId,

    @Column("type") String type,

    @Column("payload") Json payload,

    @Column("tracingspancontext") String tracingSpanContext,

    @Column("timestamp") Instant timestamp

) implements Persistable<UUID> {

  public static OutboxEventEntity of(
      String aggregateType,
      String aggregateId,
      String type,
      String payloadJson,
      String tracingSpanContext) {
    return new OutboxEventEntity(
        UUID.randomUUID(),
        aggregateType,
        aggregateId,
        type,
        Json.of(payloadJson),
        tracingSpanContext,
        Instant.now());
  }

  @Override
  public UUID getId() {
    return id;
  }

  /**
   * El id se genera en la aplicación (no en BD), así que nunca es null
   * y Spring Data R2DBC no puede inferir INSERT vs UPDATE por sí solo.
   * Como esta tabla es append-only, siempre es una fila nueva.
   */
  @Override
  @Transient
  public boolean isNew() {
    return true;
  }
}
