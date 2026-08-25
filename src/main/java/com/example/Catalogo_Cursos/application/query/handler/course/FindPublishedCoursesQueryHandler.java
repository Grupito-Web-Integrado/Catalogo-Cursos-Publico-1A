package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.FindPublishedCoursesQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FindPublishedCoursesQueryHandler
    implements QueryHandler<FindPublishedCoursesQuery, PageResult<CourseSummaryDto>> {

  private final CourseReadRepository courseReadRepository;

  public FindPublishedCoursesQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<FindPublishedCoursesQuery> queryType() {
    return FindPublishedCoursesQuery.class;
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> handle(
      FindPublishedCoursesQuery query) {

    return courseReadRepository.findPublished(
        query.page(),
        query.size());
  }
}
