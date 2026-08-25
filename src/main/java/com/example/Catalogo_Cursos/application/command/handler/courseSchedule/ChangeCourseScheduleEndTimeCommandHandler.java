package com.example.Catalogo_Cursos.application.command.handler.courseSchedule;

import com.example.Catalogo_Cursos.application.command.courseSchedule.ChangeCourseScheduleEndTimeCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseSchedule;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseScheduleId;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.repository.CourseScheduleRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChangeCourseScheduleEndTimeCommandHandler
    implements CommandHandler<ChangeCourseScheduleEndTimeCommand, Void> {

  private final CourseScheduleRepository courseScheduleRepository;
  private final OutboxExecutor outboxExecutor;

  public ChangeCourseScheduleEndTimeCommandHandler(
      CourseScheduleRepository courseScheduleRepository,
      OutboxExecutor outboxExecutor) {
    this.courseScheduleRepository = courseScheduleRepository;
    this.outboxExecutor = outboxExecutor;
  }

  @Override
  public Class<ChangeCourseScheduleEndTimeCommand> commandType() {
    return ChangeCourseScheduleEndTimeCommand.class;
  }

  @Override
  public Mono<Void> handle(
      ChangeCourseScheduleEndTimeCommand command) {

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

          schedule.changeEndTime(
              command.endTime());

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
