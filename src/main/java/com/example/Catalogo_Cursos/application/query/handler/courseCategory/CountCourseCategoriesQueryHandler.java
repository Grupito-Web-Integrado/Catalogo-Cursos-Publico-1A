package com.example.Catalogo_Cursos.application.query.handler.courseCategory;

import com.example.Catalogo_Cursos.application.port.courseCategory.CourseCategoryReadRepository;
import com.example.Catalogo_Cursos.application.query.courseCategory.CountCourseCategoriesQuery;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CountCourseCategoriesQueryHandler
    implements QueryHandler<CountCourseCategoriesQuery, Long> {

  private final CourseCategoryReadRepository repository;

  public CountCourseCategoriesQueryHandler(
      CourseCategoryReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<CountCourseCategoriesQuery> queryType() {
    return CountCourseCategoriesQuery.class;
  }

  @Override
  public Mono<Long> handle(
      CountCourseCategoriesQuery query) {

    return repository.count();
  }
}
