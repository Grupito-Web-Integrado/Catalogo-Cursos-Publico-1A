package com.example.Catalogo_Cursos.application.shared.command;

import reactor.core.publisher.Mono;

public interface CommandHandler<C extends Command, R> {
  Mono<R> handle(C command);

  Class<C> commandType();

  default Mono<Void> validate(C command) {
    return Mono.empty();
  }
}
