package com.example.Catalogo_Cursos.domain.model.shared.aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.Catalogo_Cursos.domain.model.shared.event.DomainEvent;
import com.example.Catalogo_Cursos.domain.model.shared.exception.BusinessRuleViolationException;
import com.example.Catalogo_Cursos.domain.model.shared.rule.BusinessRule;

public abstract class AggregateRoot<ID> {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AggregateRoot.class);

  protected ID id;

  private final List<DomainEvent> domainEvents = new ArrayList<>();

  protected void registerEvent(DomainEvent event) {

    Objects.requireNonNull(event, "DomainEvent cannot be null");

    if (event.aggregateId() == null) {
      throw new IllegalStateException("DomainEvent must have aggregateId");
    }

    domainEvents.add(event);

    if (log.isDebugEnabled()) {
      log.debug("[AGGREGATE ROOT] Event registered -> type={}, aggregateId={}",
          event.getClass().getSimpleName(),
          event.aggregateId());
    }
  }

  protected void checkRule(BusinessRule rule) {
    if (rule.isBroken()) {
      throw new BusinessRuleViolationException(rule.message());
    }
  }

  public List<DomainEvent> pullEvents() {

    List<DomainEvent> copy = new ArrayList<>(domainEvents);

    log.info("[AGGREGATE ROOT] Pulling events -> count={}, events={}",
        copy.size(),
        copy.stream().map(e -> e.getClass().getSimpleName()).toList());

    domainEvents.clear();

    return copy;
  }

  public ID getId() {
    return id;
  }
}
