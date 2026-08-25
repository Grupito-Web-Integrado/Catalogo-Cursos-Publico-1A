package com.example.Catalogo_Cursos.application.query.handler.courseCategory;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.port.courseCategory.CourseCategoryReadRepository;
import com.example.Catalogo_Cursos.application.query.courseCategory.FindCourseCategoriesByStatusQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FindCourseCategoriesByStatusQueryHandler
    implements QueryHandler<FindCourseCategoriesByStatusQuery, PageResult<CourseCategoryDto>> {

  private final CourseCategoryReadRepository repository;

  public FindCourseCategoriesByStatusQueryHandler(
      CourseCategoryReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<FindCourseCategoriesByStatusQuery> queryType() {
    return FindCourseCategoriesByStatusQuery.class;
  }

  @Override
  public Mono<PageResult<CourseCategoryDto>> handle(
      FindCourseCategoriesByStatusQuery query) {

    return repository.findByStatus(
        query.status(),
        query.page(),
        query.size());
  }
}
