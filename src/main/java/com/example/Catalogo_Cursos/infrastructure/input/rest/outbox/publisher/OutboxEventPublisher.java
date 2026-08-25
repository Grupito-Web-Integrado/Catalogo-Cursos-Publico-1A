package com.example.Catalogo_Cursos.infrastructure.input.rest.outbox.publisher;

import reactor.core.publisher.Mono;

public interface OutboxEventPublisher {

  Mono<Void> publishBatch(int batchSize);
}
