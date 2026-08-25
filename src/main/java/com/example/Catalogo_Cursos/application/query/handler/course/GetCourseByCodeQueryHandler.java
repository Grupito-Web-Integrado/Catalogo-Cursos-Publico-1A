package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.GetCourseByCodeQuery;
import com.example.Catalogo_Cursos.application.shared.exception.NotFoundException;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetCourseByCodeQueryHandler
    implements QueryHandler<GetCourseByCodeQuery, CourseDto> {

  private final CourseReadRepository courseReadRepository;

  public GetCourseByCodeQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<GetCourseByCodeQuery> queryType() {
    return GetCourseByCodeQuery.class;
  }

  @Override
  public Mono<CourseDto> handle(
      GetCourseByCodeQuery query) {

    return courseReadRepository
        .findByCode(query.code())
        .switchIfEmpty(
            Mono.error(
                new NotFoundException(
                    "Course not found: "
                        + query.code())));
  }
}
