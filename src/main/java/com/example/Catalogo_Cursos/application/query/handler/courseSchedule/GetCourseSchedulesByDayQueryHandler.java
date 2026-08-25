package com.example.Catalogo_Cursos.application.query.handler.courseSchedule;

import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleDto;
import com.example.Catalogo_Cursos.application.port.courseSchedule.CourseScheduleReadRepository;
import com.example.Catalogo_Cursos.application.query.courseSchedule.GetCourseSchedulesByDayQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetCourseSchedulesByDayQueryHandler
    implements QueryHandler<GetCourseSchedulesByDayQuery, PageResult<CourseScheduleDto>> {

  private final CourseScheduleReadRepository repository;

  public GetCourseSchedulesByDayQueryHandler(
      CourseScheduleReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<GetCourseSchedulesByDayQuery> queryType() {
    return GetCourseSchedulesByDayQuery.class;
  }

  @Override
  public Mono<PageResult<CourseScheduleDto>> handle(
      GetCourseSchedulesByDayQuery query) {

    return repository.findByDayOfWeek(
        query.dayOfWeek(),
        query.page(),
        query.size());
  }
}
