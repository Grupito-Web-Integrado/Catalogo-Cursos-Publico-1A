package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.CountCoursesQuery;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CountCoursesQueryHandler
    implements QueryHandler<CountCoursesQuery, Long> {

  private final CourseReadRepository courseReadRepository;

  public CountCoursesQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<CountCoursesQuery> queryType() {
    return CountCoursesQuery.class;
  }

  @Override
  public Mono<Long> handle(
      CountCoursesQuery query) {

    return courseReadRepository.count();
  }
}
