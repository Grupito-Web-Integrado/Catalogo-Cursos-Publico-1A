package com.example.Catalogo_Cursos.infrastructure.output.adapter;

import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.port.courseLocation.CourseLocationReadRepository;
import com.example.Catalogo_Cursos.infrastructure.output.cache.redis.repository.CourseLocationRedisRepository;
import com.example.Catalogo_Cursos.infrastructure.output.search.elastic.repository.CourseLocationElasticsearchRepository;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Repository
public class CachedCourseLocationReadRepository
    implements CourseLocationReadRepository {

  // =========================================================
  // TTL
  // =========================================================

  private static final Duration TTL_ENTITY = Duration.ofMinutes(10);

  private static final Duration TTL_COUNT = Duration.ofMinutes(5);

  // =========================================================
  // CACHE KEYS
  // =========================================================

  private static final String KEY_PAGE = "course-location:page:%s";

  // =========================================================
  // DEPENDENCIES
  // =========================================================

  private final CourseLocationRedisRepository redisRepository;

  private final CourseLocationElasticsearchRepository elasticsearchRepository;

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  public CachedCourseLocationReadRepository(
      CourseLocationRedisRepository redisRepository,
      CourseLocationElasticsearchRepository elasticsearchRepository) {

    this.redisRepository = redisRepository;
    this.elasticsearchRepository = elasticsearchRepository;
  }

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  @Override
  public Mono<CourseLocationDto> findById(
      UUID id) {

    return redisRepository
        .findById(id)
        .switchIfEmpty(
            elasticsearchRepository
                .findById(id)
                .flatMap(location -> cacheWrite(
                    redisRepository.save(
                        location,
                        TTL_ENTITY),
                    location)));
  }

  // =========================================================
  // COURSE RELATION
  // =========================================================

  @Override
  public Mono<PageResult<CourseLocationDto>> findByCourseId(
      UUID courseId,
      int page,
      int size) {

    String key = pageKey(
        "course",
        courseId,
        page,
        size);

    return getCachedPage(key)
        .switchIfEmpty(
            elasticsearchRepository
                .searchByCriteria(
                    null,
                    null,
                    null,
                    courseId,
                    page,
                    size)
                .flatMap(result -> cachePage(
                    key,
                    result)));
  }

  @Override
  public Flux<CourseLocationDto> findAllByCourseId(
      UUID courseId) {

    return elasticsearchRepository
        .searchByCriteria(
            null,
            null,
            null,
            courseId,
            0,
            10_000)
        .flatMapMany(
            result -> Flux.fromIterable(
                result.content()));
  }

  // =========================================================
  // LOCATION FILTERS
  // =========================================================

  @Override
  public Mono<PageResult<CourseLocationDto>> findByCity(
      String city,
      int page,
      int size) {

    String key = pageKey(
        "city",
        city,
        page,
        size);

    return getCachedPage(key)
        .switchIfEmpty(
            elasticsearchRepository
                .findByCity(
                    city,
                    page,
                    size)
                .flatMap(result -> cachePage(
                    key,
                    result)));
  }

  @Override
  public Mono<PageResult<CourseLocationDto>> findByName(
      String name,
      int page,
      int size) {

    String key = pageKey(
        "name",
        name,
        page,
        size);

    return getCachedPage(key)
        .switchIfEmpty(
            elasticsearchRepository
                .search(
                    name,
                    page,
                    size)
                .flatMap(result -> cachePage(
                    key,
                    result)));
  }

  // =========================================================
  // SEARCH
  // =========================================================

  @Override
  public Mono<PageResult<CourseLocationDto>> search(
      String text,
      int page,
      int size) {

    String key = pageKey(
        "search",
        text,
        page,
        size);

    return getCachedPage(key)
        .switchIfEmpty(
            elasticsearchRepository
                .search(
                    text,
                    page,
                    size)
                .flatMap(result -> cachePage(
                    key,
                    result)));
  }

  // =========================================================
  // COURSE + CITY
  // =========================================================

  @Override
  public Mono<PageResult<CourseLocationDto>> findByCourseAndCity(
      UUID courseId,
      String city,
      int page,
      int size) {

    String key = pageKey(
        "course-city",
        courseId + ":" + normalize(city),
        page,
        size);

    return getCachedPage(key)
        .switchIfEmpty(
            elasticsearchRepository
                .searchByCriteria(
                    null,
                    city,
                    null,
                    courseId,
                    page,
                    size)
                .flatMap(result -> cachePage(
                    key,
                    result)));
  }

  // =========================================================
  // ORDERING
  // =========================================================

  @Override
  public Mono<PageResult<CourseLocationDto>> findOrderedByCity(
      boolean ascending,
      int page,
      int size) {

    String key = pageKey(
        "ordered-city:" + ascending,
        "",
        page,
        size);

    return getCachedPage(key)
        .switchIfEmpty(
            elasticsearchRepository
                .findAll(
                    0,
                    10_000)
                .map(result -> {

                  var locations = result.content()
                      .stream()
                      .sorted((a, b) -> {

                        String cityA = a.city() == null
                            ? ""
                            : a.city();

                        String cityB = b.city() == null
                            ? ""
                            : b.city();

                        int comparison = cityA.compareToIgnoreCase(
                            cityB);

                        return ascending
                            ? comparison
                            : -comparison;
                      })
                      .toList();

                  long total = locations.size();

                  int from = Math.min(
                      page * size,
                      locations.size());

                  int to = Math.min(
                      from + size,
                      locations.size());

                  return PageResult.of(
                      locations.subList(from, to),
                      total,
                      page,
                      size);
                })
                .flatMap(result -> cachePage(
                    key,
                    result)));
  }

  // =========================================================
  // ADMIN
  // =========================================================

  @Override
  public Flux<CourseLocationDto> findAll() {

    return elasticsearchRepository
        .findAll(
            0,
            10_000)
        .flatMapMany(
            result -> Flux.fromIterable(
                result.content()));
  }

  // =========================================================
  // STATISTICS
  // =========================================================

  @Override
  public Mono<Long> count() {

    return redisRepository
        .count()
        .switchIfEmpty(
            elasticsearchRepository
                .count()
                .flatMap(count -> cacheWrite(
                    redisRepository.saveCount(
                        count,
                        TTL_COUNT),
                    count)));
  }

  @Override
  public Mono<Long> countByCourseId(
      UUID courseId) {

    return elasticsearchRepository
        .searchByCriteria(
            null,
            null,
            null,
            courseId,
            0,
            0)
        .map(PageResult::totalElements);
  }

  // =========================================================
  // CACHE PAGE
  // =========================================================

  private Mono<PageResult<CourseLocationDto>> getCachedPage(
      String key) {

    return redisRepository
        .getPage(key);
  }

  private Mono<PageResult<CourseLocationDto>> cachePage(
      String key,
      PageResult<CourseLocationDto> result) {

    return cacheWrite(
        redisRepository.cachePage(
            key,
            result),
        result);
  }

  // =========================================================
  // CACHE WRITE
  // =========================================================

  private <T> Mono<T> cacheWrite(
      Mono<Void> write,
      T value) {

    return write
        .thenReturn(value)
        .onErrorResume(
            ex -> Mono.just(value));
  }

  // =========================================================
  // KEY BUILDER
  // =========================================================

  private String pageKey(
      String type,
      Object value,
      int page,
      int size) {

    return KEY_PAGE.formatted(
        type
            + ":"
            + normalize(
                String.valueOf(value))
            + ":"
            + page
            + ":"
            + size);
  }

  private String normalize(
      String value) {

    if (value == null) {
      return "";
    }

    return value
        .trim()
        .toLowerCase()
        .replace(" ", "_");
  }
}
