package com.example.Catalogo_Cursos.infrastructure.web.controller.command;

import com.example.Catalogo_Cursos.application.command.courseSchedule.*;
import com.example.Catalogo_Cursos.application.shared.command.CommandBus;
import com.example.Catalogo_Cursos.application.shared.result.Result;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-schedules")
public class CourseScheduleCommandController {

  private final CommandBus commandBus;

  public CourseScheduleCommandController(
      CommandBus commandBus) {

    this.commandBus = commandBus;
  }

  // =========================================================
  // CREATE
  // =========================================================

  @PostMapping
  public Mono<Result<?>> create(
      @RequestBody CreateCourseScheduleBody body) {

    return commandBus.dispatch(
        new CreateCourseScheduleCommand(
            body.courseId(),
            body.dayOfWeek(),
            body.startTime(),
            body.endTime(),
            body.room()))
        .map(Result::success);
  }

  // =========================================================
  // CHANGE DAY
  // =========================================================

  @PatchMapping("/{scheduleId}/day")
  public Mono<Result<?>> changeDay(
      @PathVariable UUID scheduleId,
      @RequestBody ChangeCourseScheduleDayBody body) {

    return commandBus.dispatch(
        new ChangeCourseScheduleDayCommand(
            scheduleId,
            body.dayOfWeek()))
        .map(Result::success);
  }

  // =========================================================
  // CHANGE START TIME
  // =========================================================

  @PatchMapping("/{scheduleId}/start-time")
  public Mono<Result<?>> changeStartTime(
      @PathVariable UUID scheduleId,
      @RequestBody ChangeCourseScheduleStartTimeBody body) {

    return commandBus.dispatch(
        new ChangeCourseScheduleStartTimeCommand(
            scheduleId,
            body.startTime()))
        .map(Result::success);
  }

  // =========================================================
  // CHANGE END TIME
  // =========================================================

  @PatchMapping("/{scheduleId}/end-time")
  public Mono<Result<?>> changeEndTime(
      @PathVariable UUID scheduleId,
      @RequestBody ChangeCourseScheduleEndTimeBody body) {

    return commandBus.dispatch(
        new ChangeCourseScheduleEndTimeCommand(
            scheduleId,
            body.endTime()))
        .map(Result::success);
  }

  // =========================================================
  // CHANGE ROOM
  // =========================================================

  @PatchMapping("/{scheduleId}/room")
  public Mono<Result<?>> changeRoom(
      @PathVariable UUID scheduleId,
      @RequestBody ChangeCourseScheduleRoomBody body) {

    return commandBus.dispatch(
        new ChangeCourseScheduleRoomCommand(
            scheduleId,
            body.room()))
        .map(Result::success);
  }

  // =========================================================
  // REQUEST BODY RECORDS
  // =========================================================

  public record CreateCourseScheduleBody(
      UUID courseId,
      DayOfWeek dayOfWeek,
      LocalTime startTime,
      LocalTime endTime,
      String room) {
  }

  public record ChangeCourseScheduleDayBody(
      DayOfWeek dayOfWeek) {
  }

  public record ChangeCourseScheduleStartTimeBody(
      LocalTime startTime) {
  }

  public record ChangeCourseScheduleEndTimeBody(
      LocalTime endTime) {
  }

  public record ChangeCourseScheduleRoomBody(
      String room) {
  }
}
