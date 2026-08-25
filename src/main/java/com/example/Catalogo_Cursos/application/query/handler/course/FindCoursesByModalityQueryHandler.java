package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.FindCoursesByModalityQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FindCoursesByModalityQueryHandler
    implements QueryHandler<FindCoursesByModalityQuery, PageResult<CourseSummaryDto>> {

  private final CourseReadRepository courseReadRepository;

  public FindCoursesByModalityQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<FindCoursesByModalityQuery> queryType() {
    return FindCoursesByModalityQuery.class;
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> handle(
      FindCoursesByModalityQuery query) {

    return courseReadRepository.findByModality(
        query.modality(),
        query.page(),
        query.size());
  }
}
