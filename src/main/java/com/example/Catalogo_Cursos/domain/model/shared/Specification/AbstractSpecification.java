package com.example.Catalogo_Cursos.domain.model.shared.Specification;

public abstract class AbstractSpecification<T> implements Specification<T> {

  @Override
  public Specification<T> and(Specification<T> other) {
    return Specification.super.and(other);
  }

  @Override
  public Specification<T> or(Specification<T> other) {
    return Specification.super.or(other);
  }

  @Override
  public Specification<T> not() {
    return Specification.super.not();
  }
}
