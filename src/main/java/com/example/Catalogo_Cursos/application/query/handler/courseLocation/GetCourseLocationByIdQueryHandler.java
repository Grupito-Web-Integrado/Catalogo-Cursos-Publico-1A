package com.example.Catalogo_Cursos.application.query.handler.courseLocation;

import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationDto;
import com.example.Catalogo_Cursos.application.port.courseLocation.CourseLocationReadRepository;
import com.example.Catalogo_Cursos.application.query.courseLocation.GetCourseLocationByIdQuery;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class GetCourseLocationByIdQueryHandler
    implements QueryHandler<GetCourseLocationByIdQuery, CourseLocationDto> {

  private final CourseLocationReadRepository repository;

  public GetCourseLocationByIdQueryHandler(
      CourseLocationReadRepository repository) {

    this.repository = repository;
  }

  @Override
  public Class<GetCourseLocationByIdQuery> queryType() {
    return GetCourseLocationByIdQuery.class;
  }

  @Override
  public Mono<CourseLocationDto> handle(
      GetCourseLocationByIdQuery query) {

    return repository.findById(
        query.courseLocationId());
  }
}
