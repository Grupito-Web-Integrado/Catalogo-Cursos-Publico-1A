package com.example.Catalogo_Cursos.infrastructure.web.controller.command;

import com.example.Catalogo_Cursos.application.command.courseCategory.ActivateCourseCategoryCommand;
import com.example.Catalogo_Cursos.application.command.courseCategory.ChangeCategoryNameCommand;
import com.example.Catalogo_Cursos.application.command.courseCategory.CreateCourseCategoryCommand;
import com.example.Catalogo_Cursos.application.command.courseCategory.DeactivateCourseCategoryCommand;
import com.example.Catalogo_Cursos.application.command.courseCategory.UpdateCategoryDescriptionCommand;
import com.example.Catalogo_Cursos.application.shared.command.CommandBus;
import com.example.Catalogo_Cursos.application.shared.result.Result;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CategoryStatus;

import co.elastic.clients.elasticsearch.ml.Category;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-categories")
@Slf4j
public class CourseCategoryCommandController {

  private final CommandBus commandBus;

  public CourseCategoryCommandController(
      CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  // =========================================================
  // CREATE
  // =========================================================

  @PostMapping
  public Mono<Result<?>> create(
      @RequestBody CreateCourseCategoryBody body) {

    log.info("Peticion de Creacion de CourseCategory LLego");

    return commandBus.dispatch(
        new CreateCourseCategoryCommand(
            body.name(),
            body.description(),
            body.status()))
        .map(Result::success);
  }

  // =========================================================
  // ACTIVATE
  // =========================================================

  @PatchMapping("/{categoryId}/activate")
  public Mono<Result<?>> activate(
      @PathVariable UUID categoryId) {

    return commandBus.dispatch(
        new ActivateCourseCategoryCommand(
            categoryId))
        .map(Result::success);
  }

  // =========================================================
  // DEACTIVATE
  // =========================================================

  @PatchMapping("/{categoryId}/deactivate")
  public Mono<Result<?>> deactivate(
      @PathVariable UUID categoryId) {

    return commandBus.dispatch(
        new DeactivateCourseCategoryCommand(
            categoryId))
        .map(Result::success);
  }

  // =========================================================
  // CHANGE NAME
  // =========================================================

  @PatchMapping("/{categoryId}/name")
  public Mono<Result<?>> changeName(
      @PathVariable UUID categoryId,
      @RequestBody ChangeCategoryNameBody body) {

    return commandBus.dispatch(
        new ChangeCategoryNameCommand(
            categoryId,
            body.name()))
        .map(Result::success);
  }

  // =========================================================
  // UPDATE DESCRIPTION
  // =========================================================

  @PatchMapping("/{categoryId}/description")
  public Mono<Result<?>> updateDescription(
      @PathVariable UUID categoryId,
      @RequestBody UpdateCategoryDescriptionBody body) {

    return commandBus.dispatch(
        new UpdateCategoryDescriptionCommand(
            categoryId,
            body.description()))
        .map(Result::success);
  }

  // =========================================================
  // REQUEST BODY RECORDS
  // =========================================================

  public record CreateCourseCategoryBody(
      String name,
      String description,
      CategoryStatus status) {
  }

  public record ChangeCategoryNameBody(
      String name) {
  }

  public record UpdateCategoryDescriptionBody(
      String description) {
  }
}
