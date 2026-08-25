package com.example.Catalogo_Cursos.application.query.handler.courseCategory;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.port.courseCategory.CourseCategoryReadRepository;
import com.example.Catalogo_Cursos.application.query.courseCategory.GetCourseCategoryByIdQuery;
import com.example.Catalogo_Cursos.application.shared.exception.NotFoundException;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetCourseCategoryByIdQueryHandler
    implements QueryHandler<GetCourseCategoryByIdQuery, CourseCategoryDto> {

  private final CourseCategoryReadRepository repository;

  public GetCourseCategoryByIdQueryHandler(
      CourseCategoryReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<GetCourseCategoryByIdQuery> queryType() {
    return GetCourseCategoryByIdQuery.class;
  }

  @Override
  public Mono<CourseCategoryDto> handle(
      GetCourseCategoryByIdQuery query) {

    return repository
        .findById(query.categoryId())
        .switchIfEmpty(
            Mono.error(
                new NotFoundException(
                    "Course category not found: "
                        + query.categoryId())));
  }
}
