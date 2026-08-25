package com.example.Catalogo_Cursos.application.query.handler.courseSchedule;

import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleDto;
import com.example.Catalogo_Cursos.application.port.courseSchedule.CourseScheduleReadRepository;
import com.example.Catalogo_Cursos.application.query.courseSchedule.GetCourseSchedulesByCourseQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetCourseSchedulesByCourseQueryHandler
    implements QueryHandler<GetCourseSchedulesByCourseQuery, PageResult<CourseScheduleDto>> {

  private final CourseScheduleReadRepository repository;

  public GetCourseSchedulesByCourseQueryHandler(
      CourseScheduleReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<GetCourseSchedulesByCourseQuery> queryType() {
    return GetCourseSchedulesByCourseQuery.class;
  }

  @Override
  public Mono<PageResult<CourseScheduleDto>> handle(
      GetCourseSchedulesByCourseQuery query) {

    return repository.findAllByCourseId(
        query.courseId(),
        query.page(),
        query.size());
  }
}
