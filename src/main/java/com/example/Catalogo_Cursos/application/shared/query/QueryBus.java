package com.example.Catalogo_Cursos.application.shared.query;

import reactor.core.publisher.Mono;

public interface QueryBus {

  <R> Mono<R> dispatch(Query query);
}
