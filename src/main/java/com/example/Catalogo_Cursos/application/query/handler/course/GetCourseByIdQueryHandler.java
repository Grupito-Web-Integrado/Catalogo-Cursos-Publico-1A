package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.GetCourseByIdQuery;
import com.example.Catalogo_Cursos.application.shared.exception.NotFoundException;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetCourseByIdQueryHandler
    implements QueryHandler<GetCourseByIdQuery, CourseDto> {

  private final CourseReadRepository courseReadRepository;

  public GetCourseByIdQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<GetCourseByIdQuery> queryType() {
    return GetCourseByIdQuery.class;
  }

  @Override
  public Mono<CourseDto> handle(
      GetCourseByIdQuery query) {

    return courseReadRepository
        .findById(query.courseId())
        .switchIfEmpty(
            Mono.error(
                new NotFoundException(
                    "Course not found: "
                        + query.courseId())));
  }
}
