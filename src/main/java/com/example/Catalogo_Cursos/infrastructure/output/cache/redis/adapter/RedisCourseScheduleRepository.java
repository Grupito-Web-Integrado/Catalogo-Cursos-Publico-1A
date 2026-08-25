package com.example.Catalogo_Cursos.infrastructure.output.cache.redis.adapter;

import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.infrastructure.output.cache.redis.repository.CourseScheduleRedisRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Repository
public class RedisCourseScheduleRepository
    implements CourseScheduleRedisRepository {

  // =========================================================
  // KEYS
  // =========================================================

  private static final String KEY_ID = "course-schedule:id:%s";

  private static final String KEY_COURSE_ID = "course-schedule:course-id:%s";

  private static final String KEY_COUNT = "course-schedule:count";

  private static final String KEY_PREFIX = "course-schedule:*";

  // =========================================================
  // DEPENDENCIES
  // =========================================================

  private final ReactiveRedisTemplate<String, String> redisTemplate;

  private final ObjectMapper objectMapper;

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  public RedisCourseScheduleRepository(
      ReactiveRedisTemplate<String, String> redisTemplate,
      ObjectMapper objectMapper) {

    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  @Override
  public Mono<CourseScheduleDto> findById(
      UUID id) {

    return get(
        KEY_ID.formatted(id),
        CourseScheduleDto.class);
  }

  @Override
  public Mono<List<CourseScheduleDto>> findByCourseId(
      UUID courseId) {

    return get(
        KEY_COURSE_ID.formatted(courseId),
        CourseScheduleListWrapper.class)
        .map(CourseScheduleListWrapper::schedules);
  }

  // =========================================================
  // SAVE
  // =========================================================

  @Override
  public Mono<Void> save(
      CourseScheduleDto schedule,
      Duration ttl) {

    Mono<Void> byId = set(
        KEY_ID.formatted(schedule.id()),
        schedule,
        ttl);

    Mono<Void> byCourseId = schedule.courseId() != null
        ? Mono.empty()
        : Mono.empty();

    /*
     * La lista por courseId se maneja mediante saveByCourseId().
     *
     * No sobrescribimos aquí la lista existente porque Redis
     * no conoce automáticamente las demás schedules del curso.
     */

    return byId
        .then(byCourseId);
  }

  @Override
  public Mono<Void> saveByCourseId(
      UUID courseId,
      List<CourseScheduleDto> schedules,
      Duration ttl) {

    return set(
        KEY_COURSE_ID.formatted(courseId),
        new CourseScheduleListWrapper(schedules),
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
        .keys(KEY_PREFIX)
        .flatMap(
            key -> redisTemplate
                .delete(key))
        .then();
  }

  // =========================================================
  // PAGINATION
  // =========================================================

  @Override
  public Mono<Object> getPage(
      String key) {

    return get(
        key,
        PageWrapper.class)
        .map(PageWrapper::toPageResult);
  }

  @Override
  public Mono<Void> cachePage(
      String key,
      PageResult<CourseScheduleDto> page) {

    return set(
        key,
        PageWrapper.from(page),
        Duration.ofMinutes(10));
  }

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  @Override
  public Mono<List<CourseScheduleDto>> getAutocomplete(
      String key) {

    return get(
        key,
        CourseScheduleListWrapper.class)
        .map(CourseScheduleListWrapper::schedules);
  }

  @Override
  public Mono<Void> cacheAutocomplete(
      String key,
      List<CourseScheduleDto> list) {

    return set(
        key,
        new CourseScheduleListWrapper(list),
        Duration.ofMinutes(10));
  }

  // =========================================================
  // GENERIC GET
  // =========================================================

  private <T> Mono<T> get(
      String key,
      Class<T> type) {

    return redisTemplate
        .opsForValue()
        .get(key)
        .handle((json, sink) -> {

          try {

            T value = objectMapper.readValue(
                json,
                type);

            sink.next(value);

          } catch (JsonProcessingException e) {

            sink.error(
                new IllegalStateException(
                    "Error deserializando Redis key=" + key,
                    e));
          }
        });
  }

  // =========================================================
  // GENERIC SET
  // =========================================================

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
      List<CourseScheduleDto> content,
      long totalElements,
      int page,
      int size) {

    static PageWrapper from(
        PageResult<CourseScheduleDto> page) {

      return new PageWrapper(
          page.content(),
          page.totalElements(),
          page.page(),
          page.size());
    }

    PageResult<CourseScheduleDto> toPageResult() {

      return PageResult.of(
          content,
          totalElements,
          page,
          size);
    }
  }

  // =========================================================
  // LIST WRAPPER
  // =========================================================

  private record CourseScheduleListWrapper(
      List<CourseScheduleDto> schedules) {
  }
}
