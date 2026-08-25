package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseSuggestionDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.AutocompleteCoursesQuery;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AutocompleteCoursesQueryHandler
    implements QueryHandler<AutocompleteCoursesQuery, List<CourseSuggestionDto>> {

  private final CourseReadRepository courseReadRepository;

  public AutocompleteCoursesQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<AutocompleteCoursesQuery> queryType() {
    return AutocompleteCoursesQuery.class;
  }

  @Override
  public Mono<List<CourseSuggestionDto>> handle(
      AutocompleteCoursesQuery query) {

    return courseReadRepository.autocomplete(
        query.text(),
        query.limit());
  }
}
