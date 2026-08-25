package com.example.Catalogo_Cursos.application.query.handler.courseCategory;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.port.courseCategory.CourseCategoryReadRepository;
import com.example.Catalogo_Cursos.application.query.courseCategory.GetAllCourseCategoriesQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetAllCourseCategoriesQueryHandler
    implements QueryHandler<GetAllCourseCategoriesQuery, PageResult<CourseCategoryDto>> {

  private final CourseCategoryReadRepository repository;

  public GetAllCourseCategoriesQueryHandler(
      CourseCategoryReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<GetAllCourseCategoriesQuery> queryType() {
    return GetAllCourseCategoriesQuery.class;
  }

  @Override
  public Mono<PageResult<CourseCategoryDto>> handle(
      GetAllCourseCategoriesQuery query) {

    return repository.findAll(
        query.page(),
        query.size());
  }
}
