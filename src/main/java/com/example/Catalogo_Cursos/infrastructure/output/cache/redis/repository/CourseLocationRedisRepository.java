package com.example.Catalogo_Cursos.infrastructure.output.cache.redis.repository;

import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public interface CourseLocationRedisRepository {

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  Mono<CourseLocationDto> findById(
      UUID id);

  Mono<CourseLocationDto> findByCourseId(
      UUID courseId);

  Mono<Void> save(
      CourseLocationDto location,
      Duration ttl);

  Mono<Void> saveByCourseId(
      UUID courseId,
      CourseLocationDto location,
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

  Mono<PageResult<CourseLocationDto>> getPage(
      String key);

  Mono<Void> cachePage(
      String key,
      PageResult<CourseLocationDto> page);

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  Mono<List<CourseLocationDto>> getAutocomplete(
      String key);

  Mono<Void> cacheAutocomplete(
      String key,
      List<CourseLocationDto> list);
}
