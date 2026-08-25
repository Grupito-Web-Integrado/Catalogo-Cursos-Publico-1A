package com.example.Catalogo_Cursos.infrastructure.output.cache.redis.repository;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategorySummaryDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public interface CourseCategoryRedisRepository {

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  Mono<CourseCategoryDto> findById(UUID id);

  Mono<CourseCategoryDto> findByName(String name);

  Mono<Void> save(
      CourseCategoryDto category,
      Duration ttl);

  Mono<Void> saveByName(
      String name,
      CourseCategoryDto category,
      Duration ttl);

  // =========================================================
  // STATISTICS
  // =========================================================

  Mono<Long> count();

  Mono<Void> saveCount(
      long count,
      Duration ttl);

  // =========================================================
  // INVALIDACIÓN
  // =========================================================

  Mono<Void> evict(UUID id);

  Mono<Void> evictByName(String name);

  Mono<Void> evictAll();

  // =========================================================
  // PAGINATION
  // =========================================================

  Mono<Object> getPage(String key);

  Mono<Void> cachePage(
      String key,
      PageResult<CourseCategorySummaryDto> page);

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  Mono<List<CourseCategoryDto>> getAutocomplete(String key);

  Mono<Void> cacheAutocomplete(
      String key,
      List<CourseCategoryDto> list);
}
