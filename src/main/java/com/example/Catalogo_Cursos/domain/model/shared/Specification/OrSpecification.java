package com.example.Catalogo_Cursos.domain.model.shared.Specification;

public class OrSpecification<T> extends AbstractSpecification<T> {

  private final Specification<T> left;
  private final Specification<T> right;

  public OrSpecification(Specification<T> left, Specification<T> right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public boolean isSatisfiedBy(T candidate) {
    return left.isSatisfiedBy(candidate) || right.isSatisfiedBy(candidate);
  }
}
