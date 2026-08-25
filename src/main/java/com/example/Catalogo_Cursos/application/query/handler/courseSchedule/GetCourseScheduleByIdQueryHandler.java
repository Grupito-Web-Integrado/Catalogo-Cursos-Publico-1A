package com.example.Catalogo_Cursos.application.query.handler.courseSchedule;

import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleDto;
import com.example.Catalogo_Cursos.application.port.courseSchedule.CourseScheduleReadRepository;
import com.example.Catalogo_Cursos.application.query.courseSchedule.GetCourseScheduleByIdQuery;
import com.example.Catalogo_Cursos.application.shared.exception.NotFoundException;
import com.example.Catalogo_Cursos.application.shared.query.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetCourseScheduleByIdQueryHandler
    implements QueryHandler<GetCourseScheduleByIdQuery, CourseScheduleDto> {

  private final CourseScheduleReadRepository repository;

  public GetCourseScheduleByIdQueryHandler(
      CourseScheduleReadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Class<GetCourseScheduleByIdQuery> queryType() {
    return GetCourseScheduleByIdQuery.class;
  }

  @Override
  public Mono<CourseScheduleDto> handle(
      GetCourseScheduleByIdQuery query) {

    return repository
        .findById(query.courseScheduleId())
        .switchIfEmpty(
            Mono.error(
                new NotFoundException(
                    "Course schedule not found: "
                        + query.courseScheduleId())));
  }
}
