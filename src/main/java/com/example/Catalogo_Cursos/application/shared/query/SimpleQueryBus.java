package com.example.Catalogo_Cursos.application.shared.query;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SimpleQueryBus implements QueryBus {

  private final ApplicationContext context;

  private final Map<Class<?>, QueryHandler<?, ?>> handlers = new ConcurrentHashMap<>();

  public SimpleQueryBus(ApplicationContext context) {
    this.context = context;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> Mono<R> dispatch(Query query) {

    QueryHandler<Query, R> handler = (QueryHandler<Query, R>) handlers.computeIfAbsent(
        query.getClass(),
        clazz -> context.getBeansOfType(QueryHandler.class)
            .values()
            .stream()
            .filter(h -> h.queryType().equals(clazz))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                "No handler found for: " + clazz.getSimpleName())));

    return handler.handle(query);
  }
}
