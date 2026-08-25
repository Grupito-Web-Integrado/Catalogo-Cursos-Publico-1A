package com.example.Catalogo_Cursos.application.command.handler.courseCategory;
import com.example.Catalogo_Cursos.application.command.courseCategory.DeactivateCourseCategoryCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategory;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategoryId;
import com.example.Catalogo_Cursos.domain.model.courseCategories.repository.CourseCategoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeactivateCourseCategoryCommandHandler
        implements CommandHandler<DeactivateCourseCategoryCommand, Void> {

    private final CourseCategoryRepository courseCategoryRepository;
    private final OutboxExecutor outboxExecutor;

    public DeactivateCourseCategoryCommandHandler(
            CourseCategoryRepository courseCategoryRepository,
            OutboxExecutor outboxExecutor
    ) {
        this.courseCategoryRepository = courseCategoryRepository;
        this.outboxExecutor = outboxExecutor;
    }

    @Override
    public Class<DeactivateCourseCategoryCommand> commandType() {
        return DeactivateCourseCategoryCommand.class;
    }

    @Override
    public Mono<Void> handle(
            DeactivateCourseCategoryCommand command
    ) {

        return courseCategoryRepository
                .findById(
                        new CourseCategoryId(
                                command.courseCategoryId()
                        )
                )
                .switchIfEmpty(
                        Mono.error(
                                new IllegalArgumentException(
                                        "CourseCategory not found: "
                                                + command.courseCategoryId()
                                )
                        )
                )
                .flatMap(category -> {

                    category.deactivate();

                    return outboxExecutor.execute(
                            category,
                            courseCategoryRepository.save(category),
                            "COURSE_CATEGORY",
                            category.getId().value(),
                            CourseCategory::pullEvents
                    );
                })
                .then();
    }
}
