package com.example.Catalogo_Cursos.application.shared.outbox;

import org.springframework.stereotype.Service;

import com.example.Catalogo_Cursos.domain.model.shared.event.DomainEvent;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
public class OutboxExecutor {

  private final OutboxEventPersister outboxEventPersister;

  public OutboxExecutor(OutboxEventPersister outboxEventPersister) {
    this.outboxEventPersister = outboxEventPersister;
  }

  /**
   * Guarda el agregado y, en la misma cadena reactiva (no en la misma
   * transacción física salvo que el repositorio lo gestione), persiste
   * sus eventos de dominio en la tabla outbox para su posterior
   * captura por Debezium.
   */
  public <T> Mono<UUID> execute(
      T aggregate,
      Mono<T> saveMono,
      String aggregateType,
      UUID aggregateId,
      Function<T, List<DomainEvent>> eventExtractor) {
    List<DomainEvent> events = eventExtractor.apply(aggregate);

    return saveMono.flatMap(saved -> {
      if (events == null || events.isEmpty()) {
        return Mono.just(aggregateId);
      }
      return outboxEventPersister
          .persist(events, aggregateType, aggregateId.toString())
          .thenReturn(aggregateId);
    });
  }
}
