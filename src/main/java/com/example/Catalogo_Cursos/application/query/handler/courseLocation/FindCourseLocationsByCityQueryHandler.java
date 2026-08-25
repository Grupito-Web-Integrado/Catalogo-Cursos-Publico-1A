package com.example.Catalogo_Cursos.application.query.handler.courseLocation;

import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationDto;
import com.example.Catalogo_Cursos.application.port.courseLocation.CourseLocationReadRepository;
import com.example.Catalogo_Cursos.application.query.courseLocation.FindCourseLocationsByCityQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class FindCourseLocationsByCityQueryHandler
    implements QueryHandler<FindCourseLocationsByCityQuery, PageResult<CourseLocationDto>> {

  private final CourseLocationReadRepository repository;

  public FindCourseLocationsByCityQueryHandler(
      CourseLocationReadRepository repository) {

    this.repository = repository;
  }

  @Override
  public Class<FindCourseLocationsByCityQuery> queryType() {
    return FindCourseLocationsByCityQuery.class;
  }

  @Override
  public Mono<PageResult<CourseLocationDto>> handle(
      FindCourseLocationsByCityQuery query) {

    return repository.findByCity(
        query.city(),
        query.page(),
        query.size());
  }
}
