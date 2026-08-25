package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.FindRecentCoursesQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FindRecentCoursesQueryHandler
    implements QueryHandler<FindRecentCoursesQuery, PageResult<CourseSummaryDto>> {

  private final CourseReadRepository courseReadRepository;

  public FindRecentCoursesQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<FindRecentCoursesQuery> queryType() {
    return FindRecentCoursesQuery.class;
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> handle(
      FindRecentCoursesQuery query) {

    return courseReadRepository.findUpcoming(
        query.page(),
        query.size());
  }
}
