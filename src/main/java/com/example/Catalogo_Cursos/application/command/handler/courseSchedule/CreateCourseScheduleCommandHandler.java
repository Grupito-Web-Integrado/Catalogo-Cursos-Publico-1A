package com.example.Catalogo_Cursos.application.command.handler.courseSchedule;

import com.example.Catalogo_Cursos.application.command.courseSchedule.CreateCourseScheduleCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
import com.example.Catalogo_Cursos.domain.model.course.CourseId;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseSchedule;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.repository.CourseScheduleRepository;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class CreateCourseScheduleCommandHandler
    implements CommandHandler<CreateCourseScheduleCommand, Void> {

  private final CourseScheduleRepository scheduleRepository;
  private final OutboxExecutor outboxExecutor;

  public CreateCourseScheduleCommandHandler(
      CourseScheduleRepository scheduleRepository,
      OutboxExecutor outboxExecutor) {
    this.scheduleRepository = scheduleRepository;
    this.outboxExecutor = outboxExecutor;
  }

  @Override
  public Class<CreateCourseScheduleCommand> commandType() {
    return CreateCourseScheduleCommand.class;
  }

  @Override
  public Mono<Void> handle(
      CreateCourseScheduleCommand command) {

    CourseSchedule schedule = CourseSchedule.create(

        new CourseId(
            command.courseId()),

        command.dayOfWeek(),

        command.startTime(),

        command.endTime(),

        command.room());

    return outboxExecutor.execute(

        schedule,

        scheduleRepository.save(schedule),

        "COURSE_SCHEDULE",

        schedule.getId().value(),

        CourseSchedule::pullEvents

    ).then();
  }
}
