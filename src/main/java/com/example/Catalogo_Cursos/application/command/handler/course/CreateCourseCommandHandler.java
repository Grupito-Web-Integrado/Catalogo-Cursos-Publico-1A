package com.example.Catalogo_Cursos.application.command.handler.course;

import com.example.Catalogo_Cursos.application.command.CreateCourseCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
import com.example.Catalogo_Cursos.domain.model.course.Course;
import com.example.Catalogo_Cursos.domain.model.course.repository.CourseRepository;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class CreateCourseCommandHandler
    implements CommandHandler<CreateCourseCommand, Void> {

  private final CourseRepository courseRepository;
  private final OutboxExecutor outboxExecutor;

  public CreateCourseCommandHandler(
      CourseRepository courseRepository,
      OutboxExecutor outboxExecutor) {

    this.courseRepository = courseRepository;
    this.outboxExecutor = outboxExecutor;
  }

  @Override
  public Class<CreateCourseCommand> commandType() {
    return CreateCourseCommand.class;
  }

  @Override
  public Mono<Void> handle(
      CreateCourseCommand command) {

    Course course = Course.create(

        command.code(),

        command.name(),

        command.description(),

        command.modality(),

        command.price(),

        command.currency(),

        command.startDate(),

        command.endDate(),

        command.startTime(),

        command.durationHours(),

        command.capacity(),

        command.categoryId(),

        command.status());

    return outboxExecutor.execute(

        course,

        courseRepository.save(course),

        "COURSE",

        course.getId().value(),

        Course::pullEvents

    ).then();
  }
}
