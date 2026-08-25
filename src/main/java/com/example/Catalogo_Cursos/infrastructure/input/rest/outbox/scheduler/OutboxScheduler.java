package com.example.Catalogo_Cursos.infrastructure.input.rest.outbox.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.Catalogo_Cursos.infrastructure.input.rest.outbox.publisher.OutboxEventPublisher;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

  private final OutboxEventPublisher publisher;

  @Scheduled(fixedDelay = 2000) // cada 2 segundos
  public void run() {
    publisher.publishBatch(100)
        .onErrorResume(error -> {
          // log error
          return Mono.empty();
        })
        .subscribe();
  }
}
