package com.example.Catalogo_Cursos.application.query.handler.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseStatisticsDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.query.GetCourseStatisticsQuery;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetCourseStatisticsQueryHandler
    implements QueryHandler<GetCourseStatisticsQuery, CourseStatisticsDto> {

  private final CourseReadRepository courseReadRepository;

  public GetCourseStatisticsQueryHandler(
      CourseReadRepository courseReadRepository) {
    this.courseReadRepository = courseReadRepository;
  }

  @Override
  public Class<GetCourseStatisticsQuery> queryType() {
    return GetCourseStatisticsQuery.class;
  }

  @Override
  public Mono<CourseStatisticsDto> handle(
      GetCourseStatisticsQuery query) {

    return courseReadRepository.statistics();
  }
}
