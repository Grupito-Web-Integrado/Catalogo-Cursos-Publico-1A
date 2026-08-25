package com.example.Catalogo_Cursos.application.command.handler.courseSchedule;

import com.example.Catalogo_Cursos.application.command.courseSchedule.ChangeCourseScheduleDayCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseSchedule;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseScheduleId;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.repository.CourseScheduleRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChangeCourseScheduleDayCommandHandler
    implements CommandHandler<ChangeCourseScheduleDayCommand, Void> {

  private final CourseScheduleRepository courseScheduleRepository;
  private final OutboxExecutor outboxExecutor;

  public ChangeCourseScheduleDayCommandHandler(
      CourseScheduleRepository courseScheduleRepository,
      OutboxExecutor outboxExecutor) {
    this.courseScheduleRepository = courseScheduleRepository;
    this.outboxExecutor = outboxExecutor;
  }

  @Override
  public Class<ChangeCourseScheduleDayCommand> commandType() {
    return ChangeCourseScheduleDayCommand.class;
  }

  @Override
  public Mono<Void> handle(
      ChangeCourseScheduleDayCommand command) {

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

          schedule.changeDayOfWeek(
              command.dayOfWeek());

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
