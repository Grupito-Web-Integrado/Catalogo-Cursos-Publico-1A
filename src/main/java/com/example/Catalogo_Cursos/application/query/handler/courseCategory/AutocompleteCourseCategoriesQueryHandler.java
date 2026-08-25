package com.example.Catalogo_Cursos.application.query.handler.courseCategory;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.port.courseCategory.CourseCategoryReadRepository;
import com.example.Catalogo_Cursos.application.query.courseCategory.AutocompleteCourseCategoriesQuery;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AutocompleteCourseCategoriesQueryHandler
    implements QueryHandler<AutocompleteCourseCategoriesQuery, List<CourseCategoryDto>> {

  private final CourseCategoryReadRepository repository;

  public AutocompleteCourseCategoriesQueryHandler(
      CourseCategoryReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<AutocompleteCourseCategoriesQuery> queryType() {
    return AutocompleteCourseCategoriesQuery.class;
  }

  @Override
  public Mono<List<CourseCategoryDto>> handle(
      AutocompleteCourseCategoriesQuery query) {

    return repository.autocomplete(
        query.text(),
        query.limit());
  }
}
