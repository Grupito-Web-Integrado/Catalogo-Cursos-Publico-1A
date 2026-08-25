package com.example.Catalogo_Cursos.application.query.handler.courseCategory;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.port.courseCategory.CourseCategoryReadRepository;
import com.example.Catalogo_Cursos.application.query.courseCategory.GetCourseCategoryByNameQuery;
import com.example.Catalogo_Cursos.application.shared.exception.NotFoundException;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetCourseCategoryByNameQueryHandler
    implements QueryHandler<GetCourseCategoryByNameQuery, CourseCategoryDto> {

  private final CourseCategoryReadRepository repository;

  public GetCourseCategoryByNameQueryHandler(
      CourseCategoryReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<GetCourseCategoryByNameQuery> queryType() {
    return GetCourseCategoryByNameQuery.class;
  }

  @Override
  public Mono<CourseCategoryDto> handle(
      GetCourseCategoryByNameQuery query) {

    return repository
        .findByName(query.name())
        .switchIfEmpty(
            Mono.error(
                new NotFoundException(
                    "Course category not found: "
                        + query.name())));
  }
}
