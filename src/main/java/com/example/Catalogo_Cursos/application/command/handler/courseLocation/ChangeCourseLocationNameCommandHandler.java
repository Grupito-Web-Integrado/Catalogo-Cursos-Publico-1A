package com.example.Catalogo_Cursos.application.command.handler.courseLocation;

import com.example.Catalogo_Cursos.application.command.courseLocation.ChangeCourseLocationNameCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
import com.example.Catalogo_Cursos.domain.model.courseLocations.CourseLocation;
import com.example.Catalogo_Cursos.domain.model.courseLocations.CourseLocationId;
import com.example.Catalogo_Cursos.domain.model.courseLocations.repository.CourseLocationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChangeCourseLocationNameCommandHandler
    implements CommandHandler<ChangeCourseLocationNameCommand, Void> {

  private final CourseLocationRepository courseLocationRepository;
  private final OutboxExecutor outboxExecutor;

  public ChangeCourseLocationNameCommandHandler(
      CourseLocationRepository courseLocationRepository,
      OutboxExecutor outboxExecutor) {
    this.courseLocationRepository = courseLocationRepository;
    this.outboxExecutor = outboxExecutor;
  }

  @Override
  public Class<ChangeCourseLocationNameCommand> commandType() {
    return ChangeCourseLocationNameCommand.class;
  }

  @Override
  public Mono<Void> handle(
      ChangeCourseLocationNameCommand command) {

    return courseLocationRepository
        .findById(
            new CourseLocationId(
                command.locationId()))
        .switchIfEmpty(
            Mono.error(
                new IllegalArgumentException(
                    "CourseLocation not found: "
                        + command.locationId())))
        .flatMap(location -> {

          location.changeName(
              command.name());

          return outboxExecutor.execute(
              location,
              courseLocationRepository.save(location),
              "COURSE_LOCATION",
              location.getId().value(),
              CourseLocation::pullEvents);
        })
        .then();
  }
}
