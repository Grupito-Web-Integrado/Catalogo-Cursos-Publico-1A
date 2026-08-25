package com.example.Catalogo_Cursos.infrastructure.output.cache.redis.adapter;

import com.example.Catalogo_Cursos.application.dto.course.CourseDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseStatisticsDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseSuggestionDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.infrastructure.output.cache.redis.repository.CourseRedisRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Repository
public class RedisCourseRepository
    implements CourseRedisRepository {

  // =========================================================
  // KEYS
  // =========================================================

  private static final String KEY_ID =
      "course:id:%s";

  private static final String KEY_CODE =
      "course:code:%s";

  private static final String KEY_COUNT =
      "course:count";

  private static final String KEY_STATS =
      "course:stats";

  // =========================================================
  // CACHE CONFIG
  // =========================================================

  private static final Duration PAGE_TTL =
      Duration.ofMinutes(10);

  private static final Duration AUTOCOMPLETE_TTL =
      Duration.ofMinutes(10);

  // =========================================================
  // DEPENDENCIES
  // =========================================================

  private final ReactiveRedisTemplate<String, String> redisTemplate;

  private final ObjectMapper objectMapper;

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  public RedisCourseRepository(
      ReactiveRedisTemplate<String, String> redisTemplate,
      ObjectMapper objectMapper) {

    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  @Override
  public Mono<CourseDto> findById(UUID id) {

    return get(
        KEY_ID.formatted(id),
        CourseDto.class);
  }

  @Override
  public Mono<CourseDto> findByCode(String code) {

    return get(
        KEY_CODE.formatted(code),
        CourseDto.class);
  }

  // =========================================================
  // CACHE SINGLE OBJECT
  // =========================================================

  @Override
  public Mono<Void> save(
      CourseDto course,
      Duration ttl) {

    Mono<Void> byId =
        set(
            KEY_ID.formatted(course.id()),
            course,
            ttl);

    Mono<Void> byCode =
        course.code() != null
            ? set(
                KEY_CODE.formatted(course.code()),
                course,
                ttl)
            : Mono.empty();

    return Mono.when(
            byId,
            byCode)
        .then();
  }

  @Override
  public Mono<Void> saveByCode(
      String code,
      CourseDto course,
      Duration ttl) {

    return set(
        KEY_CODE.formatted(code),
        course,
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

  @Override
  public Mono<CourseStatisticsDto> statistics() {

    return get(
        KEY_STATS,
        CourseStatisticsDto.class);
  }

  @Override
  public Mono<Void> saveStatistics(
      CourseStatisticsDto stats,
      Duration ttl) {

    return set(
        KEY_STATS,
        stats,
        ttl);
  }

  // =========================================================
  // INVALIDACIÓN
  // =========================================================

  @Override
  public Mono<Void> evict(UUID id) {

    return redisTemplate
        .opsForValue()
        .delete(
            KEY_ID.formatted(id))
        .then();
  }

  @Override
  public Mono<Void> evictByCode(String code) {

    return redisTemplate
        .opsForValue()
        .delete(
            KEY_CODE.formatted(code))
        .then();
  }

  @Override
  public Mono<Void> evictAll() {

    return redisTemplate
        .keys("course:*")
        .flatMap(
            redisTemplate
                .opsForValue()::delete)
        .then();
  }

  // =========================================================
  // PAGINATION CACHE
  // =========================================================

  @Override
  public Mono<Object> getPage(String key) {

    return get(
        key,
        PageWrapper.class)
        .map(PageWrapper::unwrap);
  }

  @Override
  public Mono<Void> cachePage(
      String key,
      PageResult<CourseSummaryDto> page) {

    return set(
        key,
        PageWrapper.wrap(page),
        PAGE_TTL);
  }

  // =========================================================
  // AUTOCOMPLETE CACHE
  // =========================================================

  @Override
  public Mono<List<CourseSuggestionDto>> getAutocomplete(
      String key) {

    return get(
        key,
        AutocompleteWrapper.class)
        .map(AutocompleteWrapper::list);
  }

  @Override
  public Mono<Void> cacheAutocomplete(
      String key,
      List<CourseSuggestionDto> list) {

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
        .handle((json, sink) -> {

          try {

            sink.next(
                objectMapper.readValue(
                    json,
                    type));

          } catch (JsonProcessingException e) {

            sink.error(
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
            () ->
                objectMapper.writeValueAsString(value))
        .flatMap(
            json ->
                redisTemplate
                    .opsForValue()
                    .set(
                        key,
                        json,
                        ttl))
        .then();
  }

  // =========================================================
  // PAGE WRAPPER
  //
  // IMPORTANTE:
  // Evita problemas de type erasure con PageResult<T>.
  // =========================================================

  private record PageWrapper(
      List<CourseSummaryDto> content,
      long totalElements,
      int page,
      int size) {

    static PageWrapper wrap(
        PageResult<CourseSummaryDto> page) {

      return new PageWrapper(
          page.content(),
          page.totalElements(),
          page.page(),
          page.size());
    }

    static PageResult<CourseSummaryDto> unwrap(
        PageWrapper wrapper) {

      return PageResult.of(
          wrapper.content,
          wrapper.totalElements,
          wrapper.page,
          wrapper.size);
    }
  }

  // =========================================================
  // AUTOCOMPLETE WRAPPER
  // =========================================================

  private record AutocompleteWrapper(
      List<CourseSuggestionDto> list) {
  }
}
