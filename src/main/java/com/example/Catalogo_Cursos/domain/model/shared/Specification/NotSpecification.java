package com.example.Catalogo_Cursos.domain.model.shared.Specification;

public class NotSpecification<T> extends AbstractSpecification<T> {

  private final Specification<T> spec;

  public NotSpecification(Specification<T> spec) {
    this.spec = spec;
  }

  @Override
  public boolean isSatisfiedBy(T candidate) {
    return !spec.isSatisfiedBy(candidate);
  }
}
