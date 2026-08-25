package com.example.Catalogo_Cursos.infrastructure.web.controller.command;

import com.example.Catalogo_Cursos.application.command.*;
import com.example.Catalogo_Cursos.application.shared.command.CommandBus;
import com.example.Catalogo_Cursos.application.shared.result.Result;
import com.example.Catalogo_Cursos.domain.model.course.CourseModality;
import com.example.Catalogo_Cursos.domain.model.course.CourseStatus;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategoryId;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@Slf4j
public class CourseCommandController {

  private final CommandBus commandBus;

  public CourseCommandController(
      CommandBus commandBus) {

    this.commandBus = commandBus;
  }

  // =========================================================
  // CREATE
  // =========================================================

  @PostMapping
  public Mono<Result<?>> create(
      @RequestBody CreateCourseBody body) {

    log.info("Peticion de Creacion LLego");

    CourseCategoryId categoryId = CourseCategoryId.of(body.categoryId());

    CourseStatus status = CourseStatus.valueOf(body.status().toUpperCase());

    log.info(
        "Creando curso. code={}, categoryId={}, status={}",
        body.code(),
        categoryId.value(),
        status);

    return commandBus.dispatch(
        new CreateCourseCommand(
            body.code(),
            body.name(),
            body.description(),
            body.modality(),
            body.price(),
            body.currency(),
            body.startDate(),
            body.endDate(),
            body.startTime(),
            body.durationHours(),
            body.capacity(),
            categoryId,
            status))
        .map(Result::success);
  }

  // =========================================================
  // PUBLISH
  // =========================================================

  @PatchMapping("/{courseId}/publish")
  public Mono<Result<?>> publish(
      @PathVariable UUID courseId) {

    return commandBus.dispatch(
        new PublishCourseCommand(courseId))
        .map(Result::success);
  }

  // =========================================================
  // CANCEL
  // =========================================================

  @PatchMapping("/{courseId}/cancel")
  public Mono<Result<?>> cancel(
      @PathVariable UUID courseId) {

    return commandBus.dispatch(
        new CancelCourseCommand(courseId))
        .map(Result::success);
  }

  // =========================================================
  // COMPLETE
  // =========================================================

  @PatchMapping("/{courseId}/complete")
  public Mono<Result<?>> complete(
      @PathVariable UUID courseId) {

    return commandBus.dispatch(
        new CompleteCourseCommand(courseId))
        .map(Result::success);
  }

  // =========================================================
  // RESERVE SLOT
  // =========================================================

  @PatchMapping("/{courseId}/reserve-slot")
  public Mono<Result<?>> reserveSlot(
      @PathVariable UUID courseId) {

    return commandBus.dispatch(
        new ReserveCourseSlotCommand(courseId))
        .map(Result::success);
  }

  // =========================================================
  // CHANGE NAME
  // =========================================================

  @PatchMapping("/{courseId}/name")
  public Mono<Result<?>> changeName(
      @PathVariable UUID courseId,
      @RequestBody ChangeCourseNameBody body) {

    return commandBus.dispatch(
        new ChangeCourseNameCommand(
            courseId,
            body.name()))
        .map(Result::success);
  }

  // =========================================================
  // CHANGE PRICE
  // =========================================================

  @PatchMapping("/{courseId}/price")
  public Mono<Result<?>> changePrice(
      @PathVariable UUID courseId,
      @RequestBody ChangeCoursePriceBody body) {

    return commandBus.dispatch(
        new ChangeCoursePriceCommand(
            courseId,
            body.amount(),
            body.currency()))
        .map(Result::success);
  }

  // =========================================================
  // UPDATE DESCRIPTION
  // =========================================================

  @PatchMapping("/{courseId}/description")
  public Mono<Result<?>> updateDescription(
      @PathVariable UUID courseId,
      @RequestBody UpdateCourseDescriptionBody body) {

    return commandBus.dispatch(
        new UpdateCourseDescriptionCommand(
            courseId,
            body.description()))
        .map(Result::success);
  }

  // =========================================================
  // REQUEST BODY
  // =========================================================

  public record CreateCourseBody(

      String code,

      String name,

      String description,

      CourseModality modality,

      BigDecimal price,

      String currency,

      LocalDate startDate,

      LocalDate endDate,

      LocalTime startTime,

      Integer durationHours,

      Integer capacity,

      UUID categoryId,

      String status) {
  }

  public record ChangeCourseNameBody(
      String name) {
  }

  public record ChangeCoursePriceBody(
      BigDecimal amount,
      String currency) {
  }

  public record UpdateCourseDescriptionBody(
      String description) {
  }

  public record AssignCategoryBody(
      UUID categoryId) {
  }
}
