package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.FindCoursesByCriteriaQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FindCoursesByCriteriaQueryHandler
    implements QueryHandler<FindCoursesByCriteriaQuery, PageResult<CourseSummaryDto>> {

  private final CourseReadRepository courseReadRepository;

  public FindCoursesByCriteriaQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<FindCoursesByCriteriaQuery> queryType() {
    return FindCoursesByCriteriaQuery.class;
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> handle(
      FindCoursesByCriteriaQuery query) {

    return courseReadRepository.searchByCriteria(
        query.name(),
        query.code(),
        query.modality(),
        query.status(),
        query.startDateFrom(),
        query.startDateTo(),
        query.page(),
        query.size());
  }
}
