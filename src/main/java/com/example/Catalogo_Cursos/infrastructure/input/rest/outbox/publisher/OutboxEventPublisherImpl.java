package com.example.Catalogo_Cursos.infrastructure.input.rest.outbox.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.Catalogo_Cursos.infrastructure.input.rest.adapter.outbox.OutboxEventRepositoryAdapter;
import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.shared.outbox.OutboxEventEntity;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OutboxEventPublisherImpl implements OutboxEventPublisher {

  private final OutboxEventRepositoryAdapter repository;

  @Override
  public Mono<Void> publishBatch(int batchSize) {

    return repository.findPending(batchSize)
        .concatMap(this::processEvent)
        .then();
  }

  private Mono<Void> processEvent(OutboxEventEntity event) {

    return publish(event)
        .then(repository.markAsPublished(event.id()))
        .onErrorResume(error -> repository.increaseRetry(event.id(), error.getMessage())).then();
  }

  private Mono<Void> publish(OutboxEventEntity event) {
    return Mono.empty(); // Kafka / ES / Redis
  }
}
