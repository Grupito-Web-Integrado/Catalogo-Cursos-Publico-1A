package com.example.Catalogo_Cursos.domain.model.shared.event;

import java.time.Instant;

public abstract class BaseDomainEvent implements DomainEvent {

  private final EventId eventId;
  private final Instant occurredOn;

  protected BaseDomainEvent() {
    this.eventId = EventId.generate();
    this.occurredOn = Instant.now();
  }

  @Override
  public EventId eventId() {
    return eventId;
  }

  @Override
  public Instant occurredOn() {
    return occurredOn;
  }
}
