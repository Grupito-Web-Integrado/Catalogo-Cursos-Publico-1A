package com.example.Catalogo_Cursos.domain.model.shared.event;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

  EventId eventId(); // ID único del evento

  Instant occurredOn(); // cuándo ocurrió

  UUID aggregateId(); // ID del aggregate que lo produjo

  String aggregateType(); // "Book", "Author"…

  String eventType(); // enum del tipo

  int eventVersion(); // versión del payload — para evolución futura
}
