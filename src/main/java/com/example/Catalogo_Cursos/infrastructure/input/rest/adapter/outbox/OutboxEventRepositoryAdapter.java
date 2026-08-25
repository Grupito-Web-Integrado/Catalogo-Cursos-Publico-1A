package com.example.Catalogo_Cursos.infrastructure.input.rest.adapter.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.shared.outbox.OutboxEventEntity;
import com.example.Catalogo_Cursos.infrastructure.input.rest.repository.SpringDataOutboxRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventRepositoryAdapter {

  private final SpringDataOutboxRepository repository;

  // =========================
  // FIND PENDING EVENTS
  // =========================
  public Flux<OutboxEventEntity> findPending(int limit) {
    return repository.findPending(limit);
  }

  // =========================
  // MARK AS PUBLISHED
  // =========================
  public Mono<Boolean> markAsPublished(UUID id) {
    return repository.markAsPublished(id)
        .map(rows -> rows > 0);
  }

  // =========================
  // INCREASE RETRY
  // =========================
  public Mono<Boolean> increaseRetry(UUID id, String error) {
    return repository.increaseRetry(id, error)
        .map(rows -> rows > 0);
  }

}
