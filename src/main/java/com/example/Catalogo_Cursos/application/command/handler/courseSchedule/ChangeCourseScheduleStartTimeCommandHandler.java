package com.example.Catalogo_Cursos.application.command.handler.courseSchedule;

import com.example.Catalogo_Cursos.application.command.courseSchedule.ChangeCourseScheduleStartTimeCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseSchedule;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseScheduleId;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.repository.CourseScheduleRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChangeCourseScheduleStartTimeCommandHandler
    implements CommandHandler<ChangeCourseScheduleStartTimeCommand, Void> {

  private final CourseScheduleRepository courseScheduleRepository;
  private final OutboxExecutor outboxExecutor;

  public ChangeCourseScheduleStartTimeCommandHandler(
      CourseScheduleRepository courseScheduleRepository,
      OutboxExecutor outboxExecutor) {
    this.courseScheduleRepository = courseScheduleRepository;
    this.outboxExecutor = outboxExecutor;
  }

  @Override
  public Class<ChangeCourseScheduleStartTimeCommand> commandType() {
    return ChangeCourseScheduleStartTimeCommand.class;
  }

  @Override
  public Mono<Void> handle(
      ChangeCourseScheduleStartTimeCommand command) {

    return courseScheduleRepository
        .findById(
            new CourseScheduleId(
                command.scheduleId()))
        .switchIfEmpty(
            Mono.error(
                new IllegalArgumentException(
                    "CourseSchedule not found: "
                        + command.scheduleId())))
        .flatMap(schedule -> {

          schedule.changeStartTime(
              command.startTime());

          return outboxExecutor.execute(
              schedule,
              courseScheduleRepository.save(schedule),
              "COURSE_SCHEDULE",
              schedule.getId().value(),
              CourseSchedule::pullEvents);
        })
        .then();
  }
}
