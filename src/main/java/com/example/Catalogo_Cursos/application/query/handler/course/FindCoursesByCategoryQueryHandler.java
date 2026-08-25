package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.FindCoursesByCategoryQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FindCoursesByCategoryQueryHandler
    implements QueryHandler<FindCoursesByCategoryQuery, PageResult<CourseSummaryDto>> {

  private final CourseReadRepository courseReadRepository;

  public FindCoursesByCategoryQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<FindCoursesByCategoryQuery> queryType() {
    return FindCoursesByCategoryQuery.class;
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> handle(
      FindCoursesByCategoryQuery query) {

    return courseReadRepository.findByCategory(
        query.categoryId(),
        query.page(),
        query.size());
  }
}
