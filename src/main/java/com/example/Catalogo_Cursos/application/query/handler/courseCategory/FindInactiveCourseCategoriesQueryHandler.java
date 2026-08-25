package com.example.Catalogo_Cursos.application.query.handler.courseCategory;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.port.courseCategory.CourseCategoryReadRepository;
import com.example.Catalogo_Cursos.application.query.courseCategory.FindInactiveCourseCategoriesQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FindInactiveCourseCategoriesQueryHandler
    implements QueryHandler<FindInactiveCourseCategoriesQuery, PageResult<CourseCategoryDto>> {

  private final CourseCategoryReadRepository repository;

  public FindInactiveCourseCategoriesQueryHandler(
      CourseCategoryReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<FindInactiveCourseCategoriesQuery> queryType() {
    return FindInactiveCourseCategoriesQuery.class;
  }

  @Override
  public Mono<PageResult<CourseCategoryDto>> handle(
      FindInactiveCourseCategoriesQuery query) {

    return repository.findInactive(
        query.page(),
        query.size());
  }
}
