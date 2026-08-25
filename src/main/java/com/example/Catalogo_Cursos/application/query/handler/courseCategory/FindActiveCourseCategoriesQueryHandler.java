package com.example.Catalogo_Cursos.application.query.handler.courseCategory;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.port.courseCategory.CourseCategoryReadRepository;
import com.example.Catalogo_Cursos.application.query.courseCategory.FindActiveCourseCategoriesQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FindActiveCourseCategoriesQueryHandler
    implements QueryHandler<FindActiveCourseCategoriesQuery, PageResult<CourseCategoryDto>> {

  private final CourseCategoryReadRepository repository;

  public FindActiveCourseCategoriesQueryHandler(
      CourseCategoryReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<FindActiveCourseCategoriesQuery> queryType() {
    return FindActiveCourseCategoriesQuery.class;
  }

  @Override
  public Mono<PageResult<CourseCategoryDto>> handle(
      FindActiveCourseCategoriesQuery query) {

    return repository.findActive(
        query.page(),
        query.size());
  }
}
