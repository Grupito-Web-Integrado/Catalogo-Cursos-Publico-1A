package com.example.Catalogo_Cursos.infrastructure.output.cache.redis.repository;

import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public interface CourseScheduleRedisRepository {

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  Mono<CourseScheduleDto> findById(
      UUID id);

  Mono<List<CourseScheduleDto>> findByCourseId(
      UUID courseId);

  Mono<Void> save(
      CourseScheduleDto schedule,
      Duration ttl);

  Mono<Void> saveByCourseId(
      UUID courseId,
      List<CourseScheduleDto> schedules,
      Duration ttl);

  // =========================================================
  // STATISTICS
  // =========================================================

  Mono<Long> count();

  Mono<Void> saveCount(
      long count,
      Duration ttl);

  // =========================================================
  // INVALIDATION
  // =========================================================

  Mono<Void> evict(
      UUID id);

  Mono<Void> evictByCourseId(
      UUID courseId);

  Mono<Void> evictAll();

  // =========================================================
  // PAGINATION
  // =========================================================

  Mono<Object> getPage(
      String key);

  Mono<Void> cachePage(
      String key,
      PageResult<CourseScheduleDto> page);

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  Mono<List<CourseScheduleDto>> getAutocomplete(
      String key);

  Mono<Void> cacheAutocomplete(
      String key,
      List<CourseScheduleDto> list);
}
