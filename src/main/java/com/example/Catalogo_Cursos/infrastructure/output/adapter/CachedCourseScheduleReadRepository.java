package com.example.Catalogo_Cursos.infrastructure.output.adapter;

import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleDto;
import com.example.Catalogo_Cursos.application.port.courseSchedule.CourseScheduleReadRepository;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.infrastructure.output.cache.redis.repository.CourseScheduleRedisRepository;
import com.example.Catalogo_Cursos.infrastructure.output.search.elastic.repository.CourseScheduleElasticsearchRepository;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Repository
public class CachedCourseScheduleReadRepository
    implements CourseScheduleReadRepository {

  // =========================================================
  // TTL
  // =========================================================

  private static final Duration TTL_ENTITY = Duration.ofMinutes(10);

  private static final Duration TTL_PAGE = Duration.ofMinutes(10);

  private static final Duration TTL_COUNT = Duration.ofMinutes(5);

  // =========================================================
  // CACHE KEYS
  // =========================================================

  private static final String KEY_PAGE = "course-schedule:page:%s";

  // =========================================================
  // DEPENDENCIES
  // =========================================================

  private final CourseScheduleRedisRepository redisRepository;

  private final CourseScheduleElasticsearchRepository elasticsearchRepository;

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  public CachedCourseScheduleReadRepository(
      CourseScheduleRedisRepository redisRepository,
      CourseScheduleElasticsearchRepository elasticsearchRepository) {

    this.redisRepository = redisRepository;
    this.elasticsearchRepository = elasticsearchRepository;
  }

  // =========================================================
  // SINGLE LOOKUP
  // =========================================================

  @Override
  public Mono<CourseScheduleDto> findById(
      UUID id) {

    return redisRepository
        .findById(id)

        // Si Redis falla, continuamos con Elasticsearch
        .onErrorResume(
            ex -> Mono.empty())

        .switchIfEmpty(

            elasticsearchRepository
                .findById(id)

                .flatMap(
                    schedule -> cacheWrite(
                        redisRepository.save(
                            schedule,
                            TTL_ENTITY),
                        schedule)));
  }

  // =========================================================
  // COURSE RELATION
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> findByCourseId(
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
                .findByCourseId(courseId)

                .flatMap(
                    schedules -> {

                      /*
                       * Guardamos la lista completa del curso
                       * en Redis.
                       */
                      Mono<Void> cacheCourse = redisRepository
                          .saveByCourseId(
                              courseId,
                              schedules,
                              TTL_ENTITY)
                          .onErrorResume(
                              ex -> Mono.empty());

                      PageResult<CourseScheduleDto> pageResult = paginate(
                          schedules,
                          page,
                          size);

                      return cacheCourse
                          .then(
                              cachePage(
                                  key,
                                  pageResult));
                    }));
  }

  // =========================================================
  // ALL BY COURSE
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> findAllByCourseId(
      UUID courseId,
      int page,
      int size) {

    String key = pageKey(
        "course-all",
        courseId,
        page,
        size);

    return getCachedPage(key)

        .switchIfEmpty(

            elasticsearchRepository
                .findByCourseId(courseId)

                .flatMap(
                    schedules -> {

                      PageResult<CourseScheduleDto> result = paginate(
                          schedules,
                          page,
                          size);

                      return cachePage(
                          key,
                          result);
                    }));
  }

  // =========================================================
  // DAY FILTER
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> findByDayOfWeek(
      DayOfWeek dayOfWeek,
      int page,
      int size) {

    String normalizedDay = dayOfWeek.name();

    String key = pageKey(
        "day",
        normalizedDay,
        page,
        size);

    return getCachedPage(key)

        .switchIfEmpty(

            elasticsearchRepository
                .findByDayOfWeek(
                    normalizedDay,
                    page,
                    size)

                .flatMap(
                    result -> cachePage(
                        key,
                        result)));
  }

  // =========================================================
  // COURSE + DAY
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> findByCourseAndDay(
      UUID courseId,
      DayOfWeek dayOfWeek,
      int page,
      int size) {

    String key = pageKey(
        "course-day",
        courseId + ":" + dayOfWeek.name(),
        page,
        size);

    return getCachedPage(key)

        .switchIfEmpty(

            elasticsearchRepository
                .searchByCriteria(
                    courseId,
                    dayOfWeek.name(),
                    null,
                    page,
                    size)

                .flatMap(
                    result -> cachePage(
                        key,
                        result)));
  }

  // =========================================================
  // TIME RANGE
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> findByStartTimeRange(
      LocalTime from,
      LocalTime to,
      int page,
      int size) {

    String key = pageKey(
        "time-range",
        from + ":" + to,
        page,
        size);

    return getCachedPage(key)

        .switchIfEmpty(

            /*
             * El repository Elasticsearch actual no expone
             * findByStartTimeRange().
             *
             * Por eso obtenemos todos los registros y filtramos
             * en memoria.
             */
            elasticsearchRepository
                .findAll(
                    0,
                    10_000)

                .map(result ->

                result.content()
                    .stream()

                    .filter(
                        schedule -> schedule.startTime() != null)

                    .filter(
                        schedule -> {

                          LocalTime start = schedule.startTime();

                          return !start.isBefore(from)
                              && !start.isAfter(to);
                        })

                    .toList())

                .map(
                    schedules -> paginate(
                        schedules,
                        page,
                        size))

                .flatMap(
                    result -> cachePage(
                        key,
                        result)));
  }

  // =========================================================
  // ROOM
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> findByRoom(
      String room,
      int page,
      int size) {

    String key = pageKey(
        "room",
        room,
        page,
        size);

    return getCachedPage(key)

        .switchIfEmpty(

            elasticsearchRepository
                .findByRoom(
                    room,
                    page,
                    size)

                .flatMap(
                    result -> cachePage(
                        key,
                        result)));
  }

  // =========================================================
  // ORDERING
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> findOrderedByStartTime(
      boolean ascending,
      int page,
      int size) {

    String key = pageKey(
        "ordered-start-time",
        ascending,
        page,
        size);

    return getCachedPage(key)

        .switchIfEmpty(

            /*
             * El ElasticsearchRepository actual no expone
             * findOrderedByStartTime().
             *
             * Se obtiene la información y se ordena en memoria.
             */
            elasticsearchRepository
                .findAll(
                    0,
                    10_000)

                .map(result ->

                result.content()
                    .stream()

                    .sorted(
                        Comparator.comparing(
                            CourseScheduleDto::startTime,
                            Comparator.nullsLast(
                                Comparator.naturalOrder())))

                    .toList())

                .map(
                    schedules -> {

                      List<CourseScheduleDto> ordered = ascending

                          ? schedules

                          : schedules
                              .reversed();

                      return paginate(
                          ordered,
                          page,
                          size);
                    })

                .flatMap(
                    result -> cachePage(
                        key,
                        result)));
  }

  // =========================================================
  // ADMIN
  // =========================================================

  @Override
  public Flux<CourseScheduleDto> findAll() {

    return elasticsearchRepository
        .findAll(
            0,
            10_000)

        .flatMapMany(
            result -> Flux.fromIterable(
                result.content()));
  }

  // =========================================================
  // COUNT
  // =========================================================

  @Override
  public Mono<Long> count() {

    return redisRepository
        .count()

        // Redis vacío o con error
        .onErrorResume(
            ex -> Mono.empty())

        .switchIfEmpty(

            elasticsearchRepository
                .count()

                .flatMap(
                    count ->

                    cacheWrite(
                        redisRepository.saveCount(
                            count,
                            TTL_COUNT),
                        count)));
  }

  // =========================================================
  // COUNT BY COURSE
  // =========================================================

  @Override
  public Mono<Long> countByCourseId(
      UUID courseId) {

    return redisRepository
        .findByCourseId(courseId)

        /*
         * Si Redis tiene la lista:
         * simplemente contamos sus elementos.
         */
        .map(List::size)
        .map(Integer::longValue)

        /*
         * Si Redis no tiene la información,
         * consultamos Elasticsearch.
         */
        .switchIfEmpty(

            elasticsearchRepository
                .findByCourseId(courseId)

                /*
                 * Guardamos la lista obtenida en Redis
                 * y devolvemos su cantidad.
                 */
                .flatMap(
                    schedules -> {

                      long count = schedules.size();

                      return cacheWrite(
                          redisRepository.saveByCourseId(
                              courseId,
                              schedules,
                              TTL_ENTITY),
                          count);
                    }));
  }

  // =========================================================
  // CACHE PAGE - READ
  // =========================================================

  @SuppressWarnings("unchecked")
  private Mono<PageResult<CourseScheduleDto>> getCachedPage(
      String key) {

    return redisRepository
        .getPage(key)

        /*
         * Un error de Redis nunca debe romper
         * la lectura.
         */
        .onErrorResume(
            ex -> Mono.empty())

        .map(
            value -> (PageResult<CourseScheduleDto>) value);
  }

  // =========================================================
  // CACHE PAGE - WRITE
  // =========================================================

  private Mono<PageResult<CourseScheduleDto>> cachePage(
      String key,
      PageResult<CourseScheduleDto> result) {

    return redisRepository
        .cachePage(
            key,
            result)

        /*
         * Redis es cache, no fuente de verdad.
         *
         * Si falla el cache, devolvemos igualmente
         * el resultado de Elasticsearch.
         */
        .onErrorResume(
            ex -> Mono.empty())

        .thenReturn(result);
  }

  // =========================================================
  // CACHE WRITE
  // =========================================================

  private <T> Mono<T> cacheWrite(
      Mono<Void> write,
      T value) {

    return write
        .thenReturn(value)

        /*
         * Si Redis falla:
         * el resultado original sigue siendo válido.
         */
        .onErrorResume(
            ex -> Mono.just(value));
  }

  // =========================================================
  // PAGINATION
  // =========================================================

  private PageResult<CourseScheduleDto> paginate(
      List<CourseScheduleDto> schedules,
      int page,
      int size) {

    if (schedules == null) {
      return PageResult.of(
          List.of(),
          0,
          page,
          size);
    }

    if (page < 0) {
      page = 0;
    }

    if (size <= 0) {
      size = 10;
    }

    long total = schedules.size();

    int from = Math.min(
        page * size,
        schedules.size());

    int to = Math.min(
        from + size,
        schedules.size());

    List<CourseScheduleDto> content = schedules.subList(
        from,
        to);

    return PageResult.of(
        content,
        total,
        page,
        size);
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

  // =========================================================
  // NORMALIZE
  // =========================================================

  private String normalize(
      String value) {

    if (value == null) {
      return "";
    }

    return value
        .trim()
        .toLowerCase()
        .replace(" ", "_")
        .replace(":", "_");
  }
}
