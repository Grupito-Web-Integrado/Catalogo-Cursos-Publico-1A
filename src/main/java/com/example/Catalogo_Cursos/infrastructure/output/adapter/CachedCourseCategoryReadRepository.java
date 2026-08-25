package com.example.Catalogo_Cursos.infrastructure.output.adapter;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.port.courseCategory.CourseCategoryReadRepository;
import com.example.Catalogo_Cursos.infrastructure.output.cache.redis.repository.CourseCategoryRedisRepository;
import com.example.Catalogo_Cursos.infrastructure.output.search.elastic.repository.CourseCategoryElasticsearchRepository;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Repository
public class CachedCourseCategoryReadRepository
    implements CourseCategoryReadRepository {

  // =========================================================
  // TTL
  // =========================================================

  private static final Duration TTL_ENTITY = Duration.ofMinutes(10);

  private static final Duration TTL_COUNT = Duration.ofMinutes(5);

  private static final Duration TTL_AUTOCOMPLETE = Duration.ofMinutes(10);

  // =========================================================
  // DEPENDENCIES
  // =========================================================

  private final CourseCategoryRedisRepository redisRepository;

  private final CourseCategoryElasticsearchRepository elasticsearchRepository;

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  public CachedCourseCategoryReadRepository(
      CourseCategoryRedisRepository redisRepository,
      CourseCategoryElasticsearchRepository elasticsearchRepository) {

    this.redisRepository = redisRepository;
    this.elasticsearchRepository = elasticsearchRepository;
  }

  // =========================================================
  // SINGLE LOOKUPS
  //
  // REDIS -> ELASTICSEARCH -> REDIS
  // =========================================================

  @Override
  public Mono<CourseCategoryDto> findById(
      UUID id) {

    return redisRepository
        .findById(id)
        .switchIfEmpty(

            elasticsearchRepository
                .findById(id)

                .flatMap(
                    category -> cacheWrite(
                        redisRepository.save(
                            category,
                            TTL_ENTITY),
                        category)));
  }

  @Override
  public Mono<CourseCategoryDto> findByName(
      String name) {

    return redisRepository
        .findByName(name)

        .switchIfEmpty(

            elasticsearchRepository
                .findByName(name)

                .flatMap(
                    category -> cacheWrite(
                        redisRepository.saveByName(
                            name,
                            category,
                            TTL_ENTITY),
                        category)));
  }

  // =========================================================
  // SEARCH
  //
  // SIN CACHE
  // ELASTICSEARCH DIRECTO
  // =========================================================

  @Override
  public Mono<PageResult<CourseCategoryDto>> search(
      String text,
      int page,
      int size) {

    return elasticsearchRepository.search(
        text,
        page,
        size);
  }

  // =========================================================
  // STATUS
  //
  // SIN CACHE
  // =========================================================

  @Override
  public Mono<PageResult<CourseCategoryDto>> findByStatus(
      String status,
      int page,
      int size) {

    return elasticsearchRepository.findByStatus(
        status,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseCategoryDto>> findActive(
      int page,
      int size) {

    return elasticsearchRepository.findByStatus(
        "ACTIVE",
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseCategoryDto>> findInactive(
      int page,
      int size) {

    return elasticsearchRepository.findByStatus(
        "INACTIVE",
        page,
        size);
  }

  // =========================================================
  // COURSE RELATION
  //
  // SIN CACHE
  // =========================================================

  @Override
  public Mono<PageResult<CourseCategoryDto>> findByCourseId(
      UUID courseId,
      int page,
      int size) {

    return elasticsearchRepository.findByCourseId(
        courseId,
        page,
        size);
  }

  @Override
  public Flux<CourseCategoryDto> findAllByCourseId(
      UUID courseId) {

    return elasticsearchRepository.findAllByCourseId(
        courseId);
  }

  // =========================================================
  // CATEGORIES WITH COURSES
  //
  // SIN CACHE
  // =========================================================

  @Override
  public Mono<Long> countCoursesByCategoryId(
      UUID categoryId) {

    return elasticsearchRepository
        .countCoursesByCategoryId(
            categoryId);
  }

  // =========================================================
  // ORDERING
  //
  // SIN CACHE
  // =========================================================

  @Override
  public Mono<PageResult<CourseCategoryDto>> findOrderedByName(
      boolean ascending,
      int page,
      int size) {

    return elasticsearchRepository.findOrderedByName(
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
  public Mono<List<CourseCategoryDto>> autocomplete(
      String text,
      int limit) {

    String key = "course-category:auto:"
        + text.trim().toLowerCase()
        + ":"
        + limit;

    return redisRepository
        .getAutocomplete(key)

        .switchIfEmpty(

            elasticsearchRepository
                .autocomplete(
                    text,
                    limit)

                .flatMap(
                    categories -> cacheWrite(
                        redisRepository.cacheAutocomplete(
                            key,
                            categories),
                        categories)));
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

                .flatMap(
                    count -> cacheWrite(
                        redisRepository.saveCount(
                            count,
                            TTL_COUNT),
                        count)));
  }

  @Override
  public Mono<Long> countActive() {

    /*
     * Actualmente CourseCategoryRedisRepository
     * solamente tiene cache para count() general.
     *
     * Por eso countActive() va directo a Elasticsearch.
     */
    return elasticsearchRepository.countActive();
  }

  // =========================================================
  // ADMIN
  //
  // SIN CACHE
  // =========================================================

  @Override
  public Mono<PageResult<CourseCategoryDto>> findAll(
      int page,
      int size) {

    return elasticsearchRepository.findAll(
        page,
        size);
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
