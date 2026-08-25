package com.example.Catalogo_Cursos.application.query.handler.courseCategory;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.port.courseCategory.CourseCategoryReadRepository;
import com.example.Catalogo_Cursos.application.query.courseCategory.SearchCourseCategoriesQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SearchCourseCategoriesQueryHandler
    implements QueryHandler<SearchCourseCategoriesQuery, PageResult<CourseCategoryDto>> {

  private final CourseCategoryReadRepository repository;

  public SearchCourseCategoriesQueryHandler(
      CourseCategoryReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<SearchCourseCategoriesQuery> queryType() {
    return SearchCourseCategoriesQuery.class;
  }

  @Override
  public Mono<PageResult<CourseCategoryDto>> handle(
      SearchCourseCategoriesQuery query) {

    return repository.search(
        query.text(),
        query.page(),
        query.size());
  }
}
