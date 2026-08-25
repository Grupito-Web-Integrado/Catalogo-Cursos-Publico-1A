package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.FindCoursesOrderedByNameQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FindCoursesOrderedByNameQueryHandler
    implements QueryHandler<FindCoursesOrderedByNameQuery, PageResult<CourseSummaryDto>> {

  private final CourseReadRepository courseReadRepository;

  public FindCoursesOrderedByNameQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<FindCoursesOrderedByNameQuery> queryType() {
    return FindCoursesOrderedByNameQuery.class;
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> handle(
      FindCoursesOrderedByNameQuery query) {

    return courseReadRepository.findOrderedByName(
        query.ascending(),
        query.page(),
        query.size());
  }
}
