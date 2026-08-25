// package com.example.Catalogo_Cursos.application.command.handler.course;
//
// import
// com.example.Catalogo_Cursos.application.command.AssignCategoryToCourseCommand;
// import com.example.Catalogo_Cursos.application.shared.command.CommandHandler;
// import com.example.Catalogo_Cursos.application.shared.outbox.OutboxExecutor;
// import com.example.Catalogo_Cursos.domain.model.course.Course;
// import com.example.Catalogo_Cursos.domain.model.course.CourseId;
// import
// com.example.Catalogo_Cursos.domain.model.course.repository.CourseRepository;
// import
// com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategoryId;
//
// import org.springframework.stereotype.Service;
//
// import reactor.core.publisher.Mono;
//
// @Service
// public class AssignCategoryToCourseCommandHandler
// implements CommandHandler<AssignCategoryToCourseCommand, Void> {
//
// private final CourseRepository courseRepository;
// private final OutboxExecutor outboxExecutor;
//
// public AssignCategoryToCourseCommandHandler(
// CourseRepository courseRepository,
// OutboxExecutor outboxExecutor) {
// this.courseRepository = courseRepository;
// this.outboxExecutor = outboxExecutor;
// }
//
// @Override
// public Class<AssignCategoryToCourseCommand> commandType() {
// return AssignCategoryToCourseCommand.class;
// }
//
// @Override
// public Mono<Void> handle(
// AssignCategoryToCourseCommand command) {
//
// return courseRepository
//
// .findById(
// new CourseId(command.courseId()))
//
// .switchIfEmpty(
// Mono.error(
// new IllegalArgumentException(
// "Course not found: "
// + command.courseId())))
//
// .flatMap(course -> {
//
// course.assignCategory(
// new CourseCategoryId(
// command.categoryId()));
//
// return outboxExecutor.execute(
//
// course,
//
// courseRepository.save(course),
//
// "COURSE",
//
// course.getId().value(),
//
// Course::pullEvents
//
// );
// })
//
// .then();
// }
// }
