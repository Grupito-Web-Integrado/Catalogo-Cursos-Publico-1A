package com.example.Catalogo_Cursos.application.query.handler.courseLocation;

import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationDto;
import com.example.Catalogo_Cursos.application.port.courseLocation.CourseLocationReadRepository;
import com.example.Catalogo_Cursos.application.query.courseLocation.SearchCourseLocationsQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class SearchCourseLocationsQueryHandler
    implements QueryHandler<SearchCourseLocationsQuery, PageResult<CourseLocationDto>> {

  private final CourseLocationReadRepository repository;

  public SearchCourseLocationsQueryHandler(
      CourseLocationReadRepository repository) {

    this.repository = repository;
  }

  @Override
  public Class<SearchCourseLocationsQuery> queryType() {
    return SearchCourseLocationsQuery.class;
  }

  @Override
  public Mono<PageResult<CourseLocationDto>> handle(
      SearchCourseLocationsQuery query) {

    return repository.search(
        query.text(),
        query.page(),
        query.size());
  }
}
