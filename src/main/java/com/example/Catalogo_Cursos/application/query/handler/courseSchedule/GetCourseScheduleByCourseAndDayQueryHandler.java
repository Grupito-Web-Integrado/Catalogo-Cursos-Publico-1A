package com.example.Catalogo_Cursos.application.query.handler.courseSchedule;

import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleDto;
import com.example.Catalogo_Cursos.application.port.courseSchedule.CourseScheduleReadRepository;
import com.example.Catalogo_Cursos.application.query.courseSchedule.GetCourseScheduleByCourseAndDayQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetCourseScheduleByCourseAndDayQueryHandler
    implements QueryHandler<GetCourseScheduleByCourseAndDayQuery, PageResult<CourseScheduleDto>> {

  private final CourseScheduleReadRepository repository;

  public GetCourseScheduleByCourseAndDayQueryHandler(
      CourseScheduleReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<GetCourseScheduleByCourseAndDayQuery> queryType() {
    return GetCourseScheduleByCourseAndDayQuery.class;
  }

  @Override
  public Mono<PageResult<CourseScheduleDto>> handle(
      GetCourseScheduleByCourseAndDayQuery query) {

    return repository.findByCourseAndDay(
        query.courseId(),
        query.dayOfWeek(),
        query.page(),
        query.size());
  }
}
