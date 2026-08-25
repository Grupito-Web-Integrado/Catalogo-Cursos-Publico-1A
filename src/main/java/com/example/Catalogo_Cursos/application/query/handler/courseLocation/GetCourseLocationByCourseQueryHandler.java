package com.example.Catalogo_Cursos.application.query.handler.courseLocation;

import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationDto;
import com.example.Catalogo_Cursos.application.port.courseLocation.CourseLocationReadRepository;
import com.example.Catalogo_Cursos.application.query.courseLocation.GetCourseLocationByCourseQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class GetCourseLocationByCourseQueryHandler
    implements QueryHandler<GetCourseLocationByCourseQuery, PageResult<CourseLocationDto>> {

  private final CourseLocationReadRepository repository;

  public GetCourseLocationByCourseQueryHandler(
      CourseLocationReadRepository repository) {

    this.repository = repository;
  }

  @Override
  public Class<GetCourseLocationByCourseQuery> queryType() {
    return GetCourseLocationByCourseQuery.class;
  }

  @Override
  public Mono<PageResult<CourseLocationDto>> handle(
      GetCourseLocationByCourseQuery query) {

    return repository.findByCourseId(
        query.courseId(),
        query.page(),
        query.size());
  }
}
