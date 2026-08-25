package com.example.Catalogo_Cursos.application.command.handler.courseCategory;

import com.example.Catalogo_Cursos.application.command.courseCategory.ChangeCategoryNameCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategory;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategoryId;
import com.example.Catalogo_Cursos.domain.model.courseCategories.repository.CourseCategoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChangeCategoryNameCommandHandler
    implements CommandHandler<ChangeCategoryNameCommand, Void> {

  private final CourseCategoryRepository courseCategoryRepository;
  private final OutboxExecutor outboxExecutor;

  public ChangeCategoryNameCommandHandler(
      CourseCategoryRepository courseCategoryRepository,
      OutboxExecutor outboxExecutor) {
    this.courseCategoryRepository = courseCategoryRepository;
    this.outboxExecutor = outboxExecutor;
  }

  @Override
  public Class<ChangeCategoryNameCommand> commandType() {
    return ChangeCategoryNameCommand.class;
  }

  @Override
  public Mono<Void> handle(
      ChangeCategoryNameCommand command) {

    return courseCategoryRepository
        .findById(
            new CourseCategoryId(
                command.categoryId()))
        .switchIfEmpty(
            Mono.error(
                new IllegalArgumentException(
                    "CourseCategory not found: "
                        + command.categoryId())))
        .flatMap(category -> {

          category.changeName(
              command.name());

          return outboxExecutor.execute(
              category,
              courseCategoryRepository.save(category),
              "COURSE_CATEGORY",
              category.getId().value(),
              CourseCategory::pullEvents);
        })
        .then();
  }
}
