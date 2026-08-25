package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.SearchCoursesQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SearchCoursesQueryHandler
    implements QueryHandler<SearchCoursesQuery, PageResult<CourseSummaryDto>> {

  private final CourseReadRepository courseReadRepository;

  public SearchCoursesQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<SearchCoursesQuery> queryType() {
    return SearchCoursesQuery.class;
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> handle(
      SearchCoursesQuery query) {

    return courseReadRepository.search(
        query.text(),
        query.page(),
        query.size());
  }
}
