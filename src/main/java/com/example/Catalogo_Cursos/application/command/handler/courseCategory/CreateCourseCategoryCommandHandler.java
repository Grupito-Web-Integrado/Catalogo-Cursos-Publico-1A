package com.example.Catalogo_Cursos.application.command.handler.courseCategory;

import com.example.Catalogo_Cursos.application.command.courseCategory.CreateCourseCategoryCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategory;
import com.example.Catalogo_Cursos.domain.model.courseCategories.repository.CourseCategoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateCourseCategoryCommandHandler
        implements CommandHandler<CreateCourseCategoryCommand, Void> {

    private final CourseCategoryRepository courseCategoryRepository;
    private final OutboxExecutor outboxExecutor;

    public CreateCourseCategoryCommandHandler(
            CourseCategoryRepository courseCategoryRepository,
            OutboxExecutor outboxExecutor
    ) {
        this.courseCategoryRepository = courseCategoryRepository;
        this.outboxExecutor = outboxExecutor;
    }

    @Override
    public Class<CreateCourseCategoryCommand> commandType() {
        return CreateCourseCategoryCommand.class;
    }

    @Override
    public Mono<Void> handle(
            CreateCourseCategoryCommand command
    ) {

        CourseCategory category =
                CourseCategory.create(
                        command.name(),
                        command.description(),
                        command.status()
                );

        return outboxExecutor.execute(
                category,
                courseCategoryRepository.save(category),
                "COURSE_CATEGORY",
                category.getId().value(),
                CourseCategory::pullEvents
        ).then();
    }
}
