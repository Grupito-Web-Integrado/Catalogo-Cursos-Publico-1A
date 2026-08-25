package com.example.Catalogo_Cursos.application.shared.query;

import reactor.core.publisher.Mono;

public interface QueryHandler<Q extends Query, R> {
  Mono<R> handle(Q query);

  Class<Q> queryType();
}
