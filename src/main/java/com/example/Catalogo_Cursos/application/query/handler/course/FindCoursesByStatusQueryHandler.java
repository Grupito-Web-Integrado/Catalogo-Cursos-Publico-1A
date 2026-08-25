package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.FindCoursesByStatusQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FindCoursesByStatusQueryHandler
    implements QueryHandler<FindCoursesByStatusQuery, PageResult<CourseSummaryDto>> {

  private final CourseReadRepository courseReadRepository;

  public FindCoursesByStatusQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<FindCoursesByStatusQuery> queryType() {
    return FindCoursesByStatusQuery.class;
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> handle(
      FindCoursesByStatusQuery query) {

    return courseReadRepository.findByStatus(
        query.status(),
        query.page(),
        query.size());
  }
}
