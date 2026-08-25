package com.example.Catalogo_Cursos.infrastructure.output.cache.redis.adapter;

import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.infrastructure.output.cache.redis.repository.CourseLocationRedisRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Repository
public class RedisCourseLocationRepository
    implements CourseLocationRedisRepository {

  // =========================================================
  // KEYS
  // =========================================================

  private static final String KEY_ID = "course-location:id:%s";

  private static final String KEY_COURSE_ID = "course-location:course-id:%s";

  private static final String KEY_COUNT = "course-location:count";

  // =========================================================
  // CONFIG
  // =========================================================

  private static final Duration PAGE_TTL = Duration.ofMinutes(10);

  private static final Duration AUTOCOMPLETE_TTL = Duration.ofMinutes(10);

  // =========================================================
  // DEPENDENCIES
  // =========================================================

  private final ReactiveRedisTemplate<String, String> redisTemplate;

  private final ObjectMapper objectMapper;

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  public RedisCourseLocationRepository(
      ReactiveRedisTemplate<String, String> redisTemplate,
      ObjectMapper objectMapper) {

    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  @Override
  public Mono<CourseLocationDto> findById(
      UUID id) {

    return get(
        KEY_ID.formatted(id),
        CourseLocationDto.class);
  }

  @Override
  public Mono<CourseLocationDto> findByCourseId(
      UUID courseId) {

    return get(
        KEY_COURSE_ID.formatted(courseId),
        CourseLocationDto.class);
  }

  @Override
  public Mono<Void> save(
      CourseLocationDto location,
      Duration ttl) {

    Mono<Void> byId = set(
        KEY_ID.formatted(location.id()),
        location,
        ttl);

    Mono<Void> byCourseId = location.courseId() != null
        ? set(
            KEY_COURSE_ID.formatted(location.courseId()),
            location,
            ttl)
        : Mono.empty();

    return Mono.when(
        byId,
        byCourseId)
        .then();
  }

  @Override
  public Mono<Void> saveByCourseId(
      UUID courseId,
      CourseLocationDto location,
      Duration ttl) {

    return set(
        KEY_COURSE_ID.formatted(courseId),
        location,
        ttl);
  }

  // =========================================================
  // STATISTICS
  // =========================================================

  @Override
  public Mono<Long> count() {

    return redisTemplate
        .opsForValue()
        .get(KEY_COUNT)
        .map(Long::parseLong);
  }

  @Override
  public Mono<Void> saveCount(
      long count,
      Duration ttl) {

    return redisTemplate
        .opsForValue()
        .set(
            KEY_COUNT,
            String.valueOf(count),
            ttl)
        .then();
  }

  // =========================================================
  // INVALIDATION
  // =========================================================

  @Override
  public Mono<Void> evict(
      UUID id) {

    return redisTemplate
        .opsForValue()
        .delete(
            KEY_ID.formatted(id))
        .then();
  }

  @Override
  public Mono<Void> evictByCourseId(
      UUID courseId) {

    return redisTemplate
        .opsForValue()
        .delete(
            KEY_COURSE_ID.formatted(courseId))
        .then();
  }

  @Override
  public Mono<Void> evictAll() {

    return redisTemplate
        .keys("course-location:*")
        .flatMap(
            key -> redisTemplate
                .opsForValue()
                .delete(key))
        .then();
  }

  // =========================================================
  // PAGINATION
  // =========================================================

  @Override
  public Mono<PageResult<CourseLocationDto>> getPage(
      String key) {

    return get(
        key,
        PageWrapper.class)
        .map(PageWrapper::unwrap);
  }

  @Override
  public Mono<Void> cachePage(
      String key,
      PageResult<CourseLocationDto> page) {

    return set(
        key,
        PageWrapper.wrap(page),
        PAGE_TTL);
  }

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  @Override
  public Mono<List<CourseLocationDto>> getAutocomplete(
      String key) {

    return get(
        key,
        AutocompleteWrapper.class)
        .map(AutocompleteWrapper::list);
  }

  @Override
  public Mono<Void> cacheAutocomplete(
      String key,
      List<CourseLocationDto> list) {

    return set(
        key,
        new AutocompleteWrapper(list),
        AUTOCOMPLETE_TTL);
  }

  // =========================================================
  // HELPERS
  // =========================================================

  private <T> Mono<T> get(
      String key,
      Class<T> type) {

    return redisTemplate
        .opsForValue()
        .get(key)
        .flatMap(json -> {

          try {

            return Mono.just(
                objectMapper.readValue(
                    json,
                    type));

          } catch (JsonProcessingException e) {

            return Mono.error(
                new IllegalStateException(
                    "Error deserializando Redis key=" + key,
                    e));
          }
        });
  }

  private <T> Mono<Void> set(
      String key,
      T value,
      Duration ttl) {

    return Mono
        .fromCallable(
            () -> objectMapper.writeValueAsString(value))
        .flatMap(
            json -> redisTemplate
                .opsForValue()
                .set(
                    key,
                    json,
                    ttl))
        .then();
  }

  // =========================================================
  // PAGE WRAPPER
  // =========================================================

  private record PageWrapper(
      List<CourseLocationDto> content,
      long totalElements,
      int page,
      int size) {

    static PageWrapper wrap(
        PageResult<CourseLocationDto> page) {

      return new PageWrapper(
          page.content(),
          page.totalElements(),
          page.page(),
          page.size());
    }

    static PageResult<CourseLocationDto> unwrap(
        PageWrapper wrapper) {

      return PageResult.of(
          wrapper.content(),
          wrapper.totalElements(),
          wrapper.page(),
          wrapper.size());
    }
  }

  // =========================================================
  // AUTOCOMPLETE WRAPPER
  // =========================================================

  private record AutocompleteWrapper(
      List<CourseLocationDto> list) {
  }
}
