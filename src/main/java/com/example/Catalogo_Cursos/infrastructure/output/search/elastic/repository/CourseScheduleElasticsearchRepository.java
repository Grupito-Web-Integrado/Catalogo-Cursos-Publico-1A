package com.example.Catalogo_Cursos.infrastructure.output.search.elastic.repository;

import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleDto;
import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleStatisticsDto;
import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleSuggestionDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;

import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface CourseScheduleElasticsearchRepository {

  // =========================================================
  // CORE
  // =========================================================

  Mono<CourseScheduleDto> findById(
      UUID scheduleId);

  Mono<List<CourseScheduleDto>> findByCourseId(
      UUID courseId);

  Mono<PageResult<CourseScheduleDto>> findAll(
      int page,
      int size);

  // =========================================================
  // SEARCH
  // =========================================================

  Mono<PageResult<CourseScheduleDto>> search(
      String text,
      int page,
      int size);

  Mono<List<CourseScheduleSuggestionDto>> autocomplete(
      String text,
      int limit);

  Mono<PageResult<CourseScheduleDto>> searchByCriteria(
      UUID courseId,
      String dayOfWeek,
      String room,
      int page,
      int size);

  // =========================================================
  // DAY
  // =========================================================

  Mono<PageResult<CourseScheduleDto>> findByDayOfWeek(
      String dayOfWeek,
      int page,
      int size);

  // =========================================================
  // TIME
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
  // ANALYTICS
  // =========================================================

  Mono<CourseScheduleStatisticsDto> statistics();

  Mono<Long> count();

  Mono<Long> countByCourseId(
      UUID courseId);

  // =========================================================
  // INDEX
  // =========================================================

  Mono<Void> index(
      CourseScheduleDto schedule);

  Mono<Void> bulkIndex(
      List<CourseScheduleDto> schedules);

  Mono<Void> deleteFromIndex(
      UUID scheduleId);
}
