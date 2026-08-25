package com.example.Catalogo_Cursos.application.shared.command;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SimpleCommandBus implements CommandBus {

  private final ApplicationContext context;
  private final Map<Class<?>, CommandHandler<?, ?>> handlers = new ConcurrentHashMap<>();

  public SimpleCommandBus(ApplicationContext context) {
    this.context = context;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> Mono<R> dispatch(Command command) {
    CommandHandler<Command, R> handler = (CommandHandler<Command, R>) handlers.computeIfAbsent(
        command.getClass(),
        clazz -> context.getBeansOfType(CommandHandler.class)
            .values()
            .stream()
            .filter(h -> h.commandType().equals(clazz))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                "No handler found for: " + clazz.getSimpleName())));

    return handler.handle(command);
  }
}
