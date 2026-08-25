package com.example.Catalogo_Cursos.application.port.courseSchedule;

import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public interface CourseScheduleReadRepository {

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  Mono<CourseScheduleDto> findById(
      UUID id);

  // =========================================================
  // COURSE RELATION
  // =========================================================

  Mono<PageResult<CourseScheduleDto>> findByCourseId(
      UUID courseId,
      int page,
      int size);

  Mono<PageResult<CourseScheduleDto>> findAllByCourseId(
      UUID courseId,
      int page,
      int size);

  // =========================================================
  // DAY FILTERS
  // =========================================================

  Mono<PageResult<CourseScheduleDto>> findByDayOfWeek(
      DayOfWeek dayOfWeek,
      int page,
      int size);

  Mono<PageResult<CourseScheduleDto>> findByCourseAndDay(
      UUID courseId,
      DayOfWeek dayOfWeek,
      int page,
      int size);

  // =========================================================
  // TIME FILTERS
  // =========================================================

  Mono<PageResult<CourseScheduleDto>> findByStartTimeRange(
      LocalTime from,
      LocalTime to,
      int page,
      int size);

  // =========================================================
  // ROOM
  // =========================================================

  Mono<PageResult<CourseScheduleDto>> findByRoom(
      String room,
      int page,
      int size);

  // =========================================================
  // ORDERING
  // =========================================================

  Mono<PageResult<CourseScheduleDto>> findOrderedByStartTime(
      boolean ascending,
      int page,
      int size);

  // =========================================================
  // ADMIN
  // =========================================================

  Flux<CourseScheduleDto> findAll();

  // =========================================================
  // STATISTICS
  // =========================================================

  Mono<Long> count();

  Mono<Long> countByCourseId(
      UUID courseId);
}
