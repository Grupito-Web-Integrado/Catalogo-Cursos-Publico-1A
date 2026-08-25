package com.example.Catalogo_Cursos.infrastructure.output.adapter;

import com.example.Catalogo_Cursos.application.dto.course.CourseDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseStatisticsDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseSuggestionDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.port.course.CourseReadRepository;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.infrastructure.output.cache.redis.repository.CourseRedisRepository;
import com.example.Catalogo_Cursos.infrastructure.output.search.elastic.repository.CourseElasticsearchRepository;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class CachedCourseReadRepository
    implements CourseReadRepository {

  // =========================================================
  // TTL
  // =========================================================

  private static final Duration TTL_ENTITY = Duration.ofMinutes(10);

  private static final Duration TTL_COUNT = Duration.ofMinutes(5);

  private static final Duration TTL_STATISTICS = Duration.ofMinutes(5);

  // =========================================================
  // DEPENDENCIES
  // =========================================================

  private final CourseRedisRepository redisRepository;

  private final CourseElasticsearchRepository elasticsearchRepository;

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  public CachedCourseReadRepository(
      CourseRedisRepository redisRepository,
      CourseElasticsearchRepository elasticsearchRepository) {

    this.redisRepository = redisRepository;
    this.elasticsearchRepository = elasticsearchRepository;
  }

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  @Override
  public Mono<CourseDto> findById(UUID id) {

    return redisRepository
        .findById(id)
        .switchIfEmpty(
            elasticsearchRepository
                .findById(id)
                .flatMap(
                    course -> cacheWrite(
                        redisRepository.save(
                            course,
                            TTL_ENTITY),
                        course)));
  }

  @Override
  public Mono<CourseDto> findByCode(String code) {

    return redisRepository
        .findByCode(code)
        .switchIfEmpty(
            elasticsearchRepository
                .findByCode(code)
                .flatMap(
                    course -> cacheWrite(
                        redisRepository.saveByCode(
                            code,
                            course,
                            TTL_ENTITY),
                        course)));
  }

  // =========================================================
  // BASIC SEARCH
  //
  // SIN CACHE
  // =========================================================

  @Override
  public Mono<PageResult<CourseSummaryDto>> search(
      String text,
      int page,
      int size) {

    return elasticsearchRepository.search(
        text,
        page,
        size);
  }

  // =========================================================
  // SEARCH BY CRITERIA
  //
  // SIN CACHE
  // =========================================================

  @Override
  public Mono<PageResult<CourseSummaryDto>> searchByCriteria(
      String name,
      String code,
      String modality,
      String status,
      LocalDate startDateAfter,
      LocalDate startDateBefore,
      int page,
      int size) {

    return elasticsearchRepository.searchByCriteria(
        name,
        code,
        modality,
        status,
        startDateAfter,
        startDateBefore,
        page,
        size);
  }

  // =========================================================
  // FILTERS
  //
  // SIN CACHE
  // =========================================================

  @Override
  public Mono<PageResult<CourseSummaryDto>> findByModality(
      String modality,
      int page,
      int size) {

    return elasticsearchRepository.findByModality(
        modality,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findByStatus(
      String status,
      int page,
      int size) {

    return elasticsearchRepository.findByStatus(
        status,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findPublished(
      int page,
      int size) {

    return elasticsearchRepository.findPublished(
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findByCategory(
      UUID categoryId,
      int page,
      int size) {

    return elasticsearchRepository.findByCategory(
        categoryId,
        page,
        size);
  }

  // =========================================================
  // DATE FILTERS
  //
  // SIN CACHE
  // =========================================================

  @Override
  public Mono<PageResult<CourseSummaryDto>> findUpcoming(
      int page,
      int size) {

    return elasticsearchRepository.findUpcoming(
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findByStartDateRange(
      LocalDate from,
      LocalDate to,
      int page,
      int size) {

    return elasticsearchRepository.findByStartDateRange(
        from,
        to,
        page,
        size);
  }

  // =========================================================
  // AVAILABILITY
  //
  // SIN CACHE
  //
  // CourseReadRepository:
  // findAvailable()
  //
  // Elasticsearch:
  // findWithAvailableSlots()
  // =========================================================

  @Override
  public Mono<PageResult<CourseSummaryDto>> findAvailable(
      int page,
      int size) {

    return elasticsearchRepository.findWithAvailableSlots(
        page,
        size);
  }

  // =========================================================
  // ORDERING
  //
  // SIN CACHE
  // =========================================================

  @Override
  public Mono<PageResult<CourseSummaryDto>> findOrderedByName(
      boolean ascending,
      int page,
      int size) {

    return elasticsearchRepository.findOrderedByName(
        ascending,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findOrderedByStartDate(
      boolean ascending,
      int page,
      int size) {

    return elasticsearchRepository.findOrderedByStartDate(
        ascending,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findOrderedByPrice(
      boolean ascending,
      int page,
      int size) {

    return elasticsearchRepository.findOrderedByPrice(
        ascending,
        page,
        size);
  }

  // =========================================================
  // AUTOCOMPLETE
  //
  // REDIS -> ELASTICSEARCH -> REDIS
  // =========================================================

  @Override
  public Mono<List<CourseSuggestionDto>> autocomplete(
      String text,
      int limit) {

    String key = "course:auto:"
        + text.trim().toLowerCase()
        + ":"
        + limit;

    return redisRepository
        .getAutocomplete(key)
        .switchIfEmpty(
            elasticsearchRepository
                .autocomplete(text, limit)
                .flatMap(
                    suggestions -> cacheWrite(
                        redisRepository.cacheAutocomplete(
                            key,
                            suggestions),
                        suggestions)));
  }

  // =========================================================
  // COUNT
  //
  // REDIS -> ELASTICSEARCH -> REDIS
  // =========================================================

  @Override
  public Mono<Long> count() {

    return redisRepository
        .count()
        .switchIfEmpty(
            elasticsearchRepository
                .count()
                .flatMap(
                    count -> cacheWrite(
                        redisRepository.saveCount(
                            count,
                            TTL_COUNT),
                        count)));
  }

  // =========================================================
  // STATISTICS
  //
  // REDIS -> ELASTICSEARCH -> REDIS
  // =========================================================

  @Override
  public Mono<CourseStatisticsDto> statistics() {

    return redisRepository
        .statistics()
        .switchIfEmpty(
            elasticsearchRepository
                .statistics()
                .flatMap(
                    statistics -> cacheWrite(
                        redisRepository.saveStatistics(
                            statistics,
                            TTL_STATISTICS),
                        statistics)));
  }

  // =========================================================
  // ADMIN
  //
  // SIN CACHE
  // =========================================================

  @Override
  public Flux<CourseDto> findAll() {

    return elasticsearchRepository.findAll();
  }

  // =========================================================
  // HELPER
  //
  // CACHE WRITE TOLERANTE A FALLOS
  // =========================================================

  private <T> Mono<T> cacheWrite(
      Mono<Void> write,
      T value) {

    return write
        .thenReturn(value)
        .onErrorResume(
            ex -> Mono.just(value));
  }
}
