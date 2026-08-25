package com.example.Catalogo_Cursos.application.shared.command;

import reactor.core.publisher.Mono;

public interface CommandBus {

  <R> Mono<R> dispatch(Command command);
}
