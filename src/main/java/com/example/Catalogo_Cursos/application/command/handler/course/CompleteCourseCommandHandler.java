package com.example.Catalogo_Cursos.application.command.handler.course;

import com.example.Catalogo_Cursos.application.command.CompleteCourseCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
import com.example.Catalogo_Cursos.domain.model.course.Course;
import com.example.Catalogo_Cursos.domain.model.course.CourseId;
import com.example.Catalogo_Cursos.domain.model.course.repository.CourseRepository;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class CompleteCourseCommandHandler
    implements CommandHandler<CompleteCourseCommand, Void> {

  private final CourseRepository courseRepository;
  private final OutboxExecutor outboxExecutor;

  public CompleteCourseCommandHandler(
      CourseRepository courseRepository,
      OutboxExecutor outboxExecutor) {
    this.courseRepository = courseRepository;
    this.outboxExecutor = outboxExecutor;
  }

  @Override
  public Class<CompleteCourseCommand> commandType() {
    return CompleteCourseCommand.class;
  }

  @Override
  public Mono<Void> handle(
      CompleteCourseCommand command) {

    return courseRepository

        .findById(
            new CourseId(command.courseId()))

        .switchIfEmpty(
            Mono.error(
                new IllegalArgumentException(
                    "Course not found: "
                        + command.courseId())))

        .flatMap(course -> {

          course.complete();

          return outboxExecutor.execute(

              course,

              courseRepository.save(course),

              "COURSE",

              course.getId().value(),

              Course::pullEvents

        );
        })

        .then();
  }
}
