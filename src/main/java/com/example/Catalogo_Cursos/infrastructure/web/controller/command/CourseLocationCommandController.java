package com.example.Catalogo_Cursos.infrastructure.web.controller.command;

import com.example.Catalogo_Cursos.application.command.courseLocation.*;
import com.example.Catalogo_Cursos.application.shared.command.CommandBus;
import com.example.Catalogo_Cursos.application.shared.result.Result;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-locations")
public class CourseLocationCommandController {

  private final CommandBus commandBus;

  public CourseLocationCommandController(
      CommandBus commandBus) {

    this.commandBus = commandBus;
  }

  // =========================================================
  // CREATE
  // =========================================================

  @PostMapping
  public Mono<Result<?>> create(
      @RequestBody CreateCourseLocationBody body) {

    return commandBus.dispatch(
        new CreateCourseLocationCommand(
            body.courseId(),
            body.name(),
            body.address(),
            body.city(),
            body.reference(),
            body.capacity()))
        .map(Result::success);
  }

  // =========================================================
  // CHANGE NAME
  // =========================================================

  @PatchMapping("/{locationId}/name")
  public Mono<Result<?>> changeName(
      @PathVariable UUID locationId,
      @RequestBody ChangeCourseLocationNameBody body) {

    return commandBus.dispatch(
        new ChangeCourseLocationNameCommand(
            locationId,
            body.name()))
        .map(Result::success);
  }

  // =========================================================
  // CHANGE ADDRESS
  // =========================================================

  @PatchMapping("/{locationId}/address")
  public Mono<Result<?>> changeAddress(
      @PathVariable UUID locationId,
      @RequestBody ChangeCourseLocationAddressBody body) {

    return commandBus.dispatch(
        new ChangeCourseLocationAddressCommand(
            locationId,
            body.address()))
        .map(Result::success);
  }

  // =========================================================
  // CHANGE CITY
  // =========================================================

  @PatchMapping("/{locationId}/city")
  public Mono<Result<?>> changeCity(
      @PathVariable UUID locationId,
      @RequestBody ChangeCourseLocationCityBody body) {

    return commandBus.dispatch(
        new ChangeCourseLocationCityCommand(
            locationId,
            body.city()))
        .map(Result::success);
  }

  // =========================================================
  // CHANGE REFERENCE
  // =========================================================

  @PatchMapping("/{locationId}/reference")
  public Mono<Result<?>> changeReference(
      @PathVariable UUID locationId,
      @RequestBody ChangeCourseLocationReferenceBody body) {

    return commandBus.dispatch(
        new ChangeCourseLocationReferenceCommand(
            locationId,
            body.reference()))
        .map(Result::success);
  }

  // =========================================================
  // CHANGE CAPACITY
  // =========================================================

  @PatchMapping("/{locationId}/capacity")
  public Mono<Result<?>> changeCapacity(
      @PathVariable UUID locationId,
      @RequestBody ChangeCourseLocationCapacityBody body) {

    return commandBus.dispatch(
        new ChangeCourseLocationCapacityCommand(
            locationId,
            body.capacity()))
        .map(Result::success);
  }

  // =========================================================
  // REQUEST BODY RECORDS
  // =========================================================

  public record CreateCourseLocationBody(
      UUID courseId,
      String name,
      String address,
      String city,
      String reference,
      Integer capacity) {
  }

  public record ChangeCourseLocationNameBody(
      String name) {
  }

  public record ChangeCourseLocationAddressBody(
      String address) {
  }

  public record ChangeCourseLocationCityBody(
      String city) {
  }

  public record ChangeCourseLocationReferenceBody(
      String reference) {
  }

  public record ChangeCourseLocationCapacityBody(
      Integer capacity) {
  }
}
