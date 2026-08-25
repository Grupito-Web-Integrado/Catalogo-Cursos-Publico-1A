package com.example.Catalogo_Cursos.application.command.handler.courseLocation;
import com.example.Catalogo_Cursos.application.command.courseLocation.CreateCourseLocationCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
import com.example.Catalogo_Cursos.domain.model.course.CourseId;
import com.example.Catalogo_Cursos.domain.model.courseLocations.CourseLocation;
import com.example.Catalogo_Cursos.domain.model.courseLocations.repository.CourseLocationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateCourseLocationCommandHandler
        implements CommandHandler<CreateCourseLocationCommand, Void> {

    private final CourseLocationRepository courseLocationRepository;
    private final OutboxExecutor outboxExecutor;

    public CreateCourseLocationCommandHandler(
            CourseLocationRepository courseLocationRepository,
            OutboxExecutor outboxExecutor
    ) {
        this.courseLocationRepository = courseLocationRepository;
        this.outboxExecutor = outboxExecutor;
    }

    @Override
    public Class<CreateCourseLocationCommand> commandType() {
        return CreateCourseLocationCommand.class;
    }

    @Override
    public Mono<Void> handle(
            CreateCourseLocationCommand command
    ) {

        CourseLocation location =
                CourseLocation.create(
                        new CourseId(
                                command.courseId()
                        ),
                        command.name(),
                        command.address(),
                        command.city(),
                        command.reference(),
                        command.capacity()
                );

        return outboxExecutor.execute(
                location,
                courseLocationRepository.save(location),
                "COURSE_LOCATION",
                location.getId().value(),
                CourseLocation::pullEvents
        ).then();
    }
}
