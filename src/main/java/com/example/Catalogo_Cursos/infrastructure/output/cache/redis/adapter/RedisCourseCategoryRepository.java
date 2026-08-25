package com.example.Catalogo_Cursos.infrastructure.output.cache.redis.adapter;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategorySummaryDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.infrastructure.output.cache.redis.repository.CourseCategoryRedisRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Repository
public class RedisCourseCategoryRepository
    implements CourseCategoryRedisRepository {

  // =========================================================
  // KEYS
  // =========================================================

  private static final String KEY_ID = "course-category:id:%s";

  private static final String KEY_NAME = "course-category:name:%s";

  private static final String KEY_COUNT = "course-category:count";

  private static final String KEY_PAGE = "course-category:page:%s";

  private static final String KEY_AUTOCOMPLETE = "course-category:auto:%s";

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

  public RedisCourseCategoryRepository(
      ReactiveRedisTemplate<String, String> redisTemplate,
      ObjectMapper objectMapper) {

    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  @Override
  public Mono<CourseCategoryDto> findById(UUID id) {

    return get(
        KEY_ID.formatted(id),
        CourseCategoryDto.class);
  }

  @Override
  public Mono<CourseCategoryDto> findByName(String name) {

    return get(
        KEY_NAME.formatted(name),
        CourseCategoryDto.class);
  }

  @Override
  public Mono<Void> save(
      CourseCategoryDto category,
      Duration ttl) {

    Mono<Void> byId = set(
        KEY_ID.formatted(category.id()),
        category,
        ttl);

    Mono<Void> byName = category.name() != null
        ? set(
            KEY_NAME.formatted(category.name()),
            category,
            ttl)
        : Mono.empty();

    return Mono.when(
        byId,
        byName)
        .then();
  }

  @Override
  public Mono<Void> saveByName(
      String name,
      CourseCategoryDto category,
      Duration ttl) {

    return set(
        KEY_NAME.formatted(name),
        category,
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
  public Mono<Void> evictByName(String name) {

    return redisTemplate
        .opsForValue()
        .delete(
            KEY_NAME.formatted(name))
        .then();
  }

  @Override
  public Mono<Void> evictAll() {

    return redisTemplate
        .keys("course-category:*")
        .flatMap(
            redisTemplate
                .opsForValue()::delete)
        .then();
  }

  // =========================================================
  // PAGINATION
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
      PageResult<CourseCategorySummaryDto> page) {

    return set(
        key,
        PageWrapper.wrap(page),
        PAGE_TTL);
  }

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  @Override
  public Mono<List<CourseCategoryDto>> getAutocomplete(
      String key) {

    return get(
        key,
        AutocompleteWrapper.class)
        .map(AutocompleteWrapper::list);
  }

  @Override
  public Mono<Void> cacheAutocomplete(
      String key,
      List<CourseCategoryDto> list) {

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
  // WRAPPERS
  //
  // IMPORTANTE:
  // Se utilizan para evitar problemas de type erasure
  // con PageResult<T> y List<T>.
  // =========================================================

  private record PageWrapper(
      List<CourseCategorySummaryDto> content,
      long totalElements,
      int page,
      int size) {

    static PageWrapper wrap(
        PageResult<CourseCategorySummaryDto> page) {

      return new PageWrapper(
          page.content(),
          page.totalElements(),
          page.page(),
          page.size());
    }

    static PageResult<CourseCategorySummaryDto> unwrap(
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
      List<CourseCategoryDto> list) {
  }
}
