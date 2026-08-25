package com.example.Catalogo_Cursos.infrastructure.output.cache.redis.repository;

import com.example.Catalogo_Cursos.application.dto.course.CourseDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseStatisticsDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseSuggestionDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public interface CourseRedisRepository {

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  Mono<CourseDto> findById(

      UUID id

  );

  Mono<CourseDto> findByCode(

      String code

  );

  // =========================================================
  // CACHE SINGLE OBJECT
  // =========================================================

  Mono<Void> save(

      CourseDto course,

      Duration ttl

  );

  Mono<Void> saveByCode(

      String code,

      CourseDto course,

      Duration ttl

  );

  // =========================================================
  // STATISTICS
  // =========================================================

  Mono<Long> count();

  Mono<Void> saveCount(

      long count,

      Duration ttl

  );

  Mono<CourseStatisticsDto> statistics();

  Mono<Void> saveStatistics(

      CourseStatisticsDto stats,

      Duration ttl

  );

  // =========================================================
  // INVALIDACIÓN
  // =========================================================

  Mono<Void> evict(

      UUID id

  );

  Mono<Void> evictByCode(

      String code

  );

  Mono<Void> evictAll();

  // =========================================================
  // PAGINATION CACHE
  // =========================================================

  Mono<Object> getPage(

      String key

  );

  Mono<Void> cachePage(

      String key,

      PageResult<CourseSummaryDto> page

  );

  // =========================================================
  // AUTOCOMPLETE CACHE
  // =========================================================

  Mono<List<CourseSuggestionDto>> getAutocomplete(

      String key

  );

  Mono<Void> cacheAutocomplete(

      String key,

      List<CourseSuggestionDto> list

  );
}
