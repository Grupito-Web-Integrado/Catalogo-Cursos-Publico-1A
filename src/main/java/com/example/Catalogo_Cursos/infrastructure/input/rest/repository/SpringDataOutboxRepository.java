package com.example.Catalogo_Cursos.infrastructure.input.rest.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.shared.outbox.OutboxEventEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SpringDataOutboxRepository
    extends ReactiveCrudRepository<OutboxEventEntity, UUID> {

  @Query("""
          SELECT *
          FROM outbox_events
          WHERE status = 'PENDING'
          ORDER BY created_at ASC
          LIMIT :limit
      """)
  Flux<OutboxEventEntity> findPending(int limit);

  @Query("""
          UPDATE outbox_events
          SET status = 'PUBLISHED',
              published_at = NOW()
          WHERE event_id = :id
      """)
  Mono<Integer> markAsPublished(UUID id);

  @Query("""
          UPDATE outbox_events
          SET retry_count = retry_count + 1,
              last_error = :error,
              status = CASE
                  WHEN retry_count >= 3 THEN 'FAILED'
                  ELSE 'PENDING'
              END
          WHERE event_id = :id
      """)
  Mono<Integer> increaseRetry(UUID id, String error);
}
