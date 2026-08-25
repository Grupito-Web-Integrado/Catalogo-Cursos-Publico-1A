package com.example.Catalogo_Cursos.application.command.handler.courseSchedule;

import com.example.Catalogo_Cursos.application.command.courseSchedule.ChangeCourseScheduleRoomCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseSchedule;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseScheduleId;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.repository.CourseScheduleRepository;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class ChangeCourseScheduleRoomCommandHandler
    implements CommandHandler<ChangeCourseScheduleRoomCommand, Void> {

  private final CourseScheduleRepository scheduleRepository;
  private final OutboxExecutor outboxExecutor;

  public ChangeCourseScheduleRoomCommandHandler(
      CourseScheduleRepository scheduleRepository,
      OutboxExecutor outboxExecutor) {
    this.scheduleRepository = scheduleRepository;
    this.outboxExecutor = outboxExecutor;
  }

  @Override
  public Class<ChangeCourseScheduleRoomCommand> commandType() {
    return ChangeCourseScheduleRoomCommand.class;
  }

  @Override
  public Mono<Void> handle(
      ChangeCourseScheduleRoomCommand command) {

    return scheduleRepository

        .findById(
            new CourseScheduleId(
                command.scheduleId()))

        .switchIfEmpty(
            Mono.error(
                new IllegalArgumentException(
                    "Course schedule not found: "
                        + command.scheduleId())))

        .flatMap(schedule -> {

          schedule.changeRoom(
              command.room());

          return outboxExecutor.execute(

              schedule,

              scheduleRepository.save(
                  schedule),

              "COURSE_SCHEDULE",

              schedule.getId().value(),

              CourseSchedule::pullEvents

        );
        })

        .then();
  }
}
