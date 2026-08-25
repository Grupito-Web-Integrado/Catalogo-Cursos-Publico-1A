package com.example.Catalogo_Cursos.application.shared.outbox;

import com.example.Catalogo_Cursos.domain.model.shared.event.DomainEvent;
import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.shared.outbox.OutboxEventEntity;
import com.example.Catalogo_Cursos.infrastructure.input.rest.repository.SpringDataOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.Span;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OutboxEventPersister {

  private static final String CONTENT_TYPE_JSON = "application/json";

  private final SpringDataOutboxRepository repository;
  private final ObjectMapper objectMapper;

  public OutboxEventPersister(
      SpringDataOutboxRepository repository,
      ObjectMapper objectMapper) {
    this.repository = repository;
    this.objectMapper = objectMapper;
  }

  public Mono<Void> persist(
      List<DomainEvent> events,
      String aggregateType,
      String aggregateId) {
    if (events == null || events.isEmpty()) {
      return Mono.empty();
    }

    String traceContext = currentTraceParent();

    return Mono.fromCallable(() -> events.stream()
        .map(event -> toOutboxEntity(event, aggregateType, aggregateId, traceContext))
        .toList())
        .flatMapMany(repository::saveAll)
        .then();
  }

  private OutboxEventEntity toOutboxEntity(
      DomainEvent event,
      String aggregateType,
      String aggregateId,
      String traceContext) {
    try {
      String payloadJson = objectMapper.writeValueAsString(event);
      return OutboxEventEntity.of(
          aggregateType,
          aggregateId,
          event.eventType(),
          payloadJson,
          traceContext);
    } catch (Exception e) {
      throw new OutboxSerializationException(
          "Error serializing event: " + event.getClass().getName(),
          e);
    }
  }

  /**
   * Captura el contexto de trace W3C (traceparent) del Span activo
   * de OpenTelemetry para propagarlo hacia el consumer vía Kafka.
   */
  private String currentTraceParent() {
    Span currentSpan = Span.current();
    if (currentSpan == null || !currentSpan.getSpanContext().isValid()) {
      return null;
    }
    var ctx = currentSpan.getSpanContext();
    return "00-" + ctx.getTraceId() + "-" + ctx.getSpanId() + "-" +
        (ctx.isSampled() ? "01" : "00");
  }
}
