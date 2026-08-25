package com.example.Catalogo_Cursos.infrastructure.output.search.elastic.adapter;

import co.elastic.clients.json.JsonData;
import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;

import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleDto;
import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleStatisticsDto;
import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleSuggestionDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.infrastructure.output.search.elastic.repository.CourseScheduleElasticsearchRepository;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class CourseScheduleElasticsearchRepositoryImpl
    implements CourseScheduleElasticsearchRepository {

  // =========================================================
  // INDEX
  // =========================================================

  private static final String INDEX = "course_schedules";

  // =========================================================
  // CLIENT
  // =========================================================

  private final ElasticsearchAsyncClient client;

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  public CourseScheduleElasticsearchRepositoryImpl(
      ElasticsearchAsyncClient client) {

    this.client = client;
  }

  // =========================================================
  // FIND BY ID
  // =========================================================

  @Override
  public Mono<CourseScheduleDto> findById(
      UUID scheduleId) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q
            .term(t -> t
                .field("id")
                .value(scheduleId.toString())))
        .size(1));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(
            Schedulers.boundedElastic())
        .mapNotNull(
            response -> response.hits()
                .hits()
                .stream()
                .findFirst()
                .map(Hit::source)
                .map(this::asMap)
                .map(this::toDto)
                .orElse(null));
  }

  // =========================================================
  // FIND BY COURSE
  // =========================================================

  @Override
  public Mono<List<CourseScheduleDto>> findByCourseId(
      UUID courseId) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q
            .term(t -> t
                .field("courseId")
                .value(courseId.toString())))
        .size(10_000));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(
            Schedulers.boundedElastic())
        .map(response -> response.hits()
            .hits()
            .stream()
            .map(Hit::source)
            .map(this::asMap)
            .map(this::toDto)
            .collect(Collectors.toList()));
  }

  // =========================================================
  // FIND ALL
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> findAll(
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .from(page * size)
        .size(size));

    return executeSearch(
        request,
        page,
        size);
  }

  // =========================================================
  // SEARCH
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> search(
      String text,
      int page,
      int size) {

    if (text == null || text.isBlank()) {
      return findAll(page, size);
    }

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q
            .multiMatch(mm -> mm
                .query(text)
                .fields(
                    "room",
                    "dayOfWeek")))
        .from(page * size)
        .size(size));

    return executeSearch(
        request,
        page,
        size);
  }

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  @Override
  public Mono<List<CourseScheduleSuggestionDto>> autocomplete(
      String text,
      int limit) {

    if (text == null || text.isBlank()) {
      return Mono.just(List.of());
    }

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q
            .multiMatch(mm -> mm
                .query(text)
                .fields(
                    "room",
                    "dayOfWeek")))
        .size(limit));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(
            Schedulers.boundedElastic())
        .map(response -> response.hits()
            .hits()
            .stream()
            .map(Hit::source)
            .map(this::asMap)
            .map(this::toSuggestion)
            .collect(Collectors.toList()));
  }

  // =========================================================
  // SEARCH CRITERIA
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> searchByCriteria(
      UUID courseId,
      String dayOfWeek,
      String room,
      int page,
      int size) {

    BoolQuery.Builder bool = new BoolQuery.Builder();

    if (courseId != null) {

      bool.filter(
          f -> f.term(
              t -> t
                  .field("courseId")
                  .value(courseId.toString())));
    }

    if (dayOfWeek != null
        && !dayOfWeek.isBlank()) {

      bool.filter(
          f -> f.term(
              t -> t
                  .field("dayOfWeek")
                  .value(dayOfWeek)));
    }

    if (room != null
        && !room.isBlank()) {

      bool.must(
          m -> m.match(
              mt -> mt
                  .field("room")
                  .query(room)));
    }

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q
            .bool(bool.build()))
        .from(page * size)
        .size(size));

    return executeSearch(
        request,
        page,
        size);
  }

  // =========================================================
  // FIND BY DAY
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> findByDayOfWeek(
      String dayOfWeek,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q
            .term(t -> t
                .field("dayOfWeek")
                .value(dayOfWeek)))
        .from(page * size)
        .size(size));

    return executeSearch(
        request,
        page,
        size);
  }

  // =========================================================
  // FIND BY TIME RANGE
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> findByStartTimeRange(
      LocalTime from,
      LocalTime to,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q
            .range(r -> r
                .field("startTime")
                .gte(JsonData.of(from.toString()))
                .lte(JsonData.of(to.toString()))))
        .from(page * size)
        .size(size));

    return executeSearch(
        request,
        page,
        size);
  }

  // =========================================================
  // FIND BY ROOM
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> findByRoom(
      String room,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q
            .match(m -> m
                .field("room")
                .query(room)))
        .from(page * size)
        .size(size));

    return executeSearch(
        request,
        page,
        size);
  }

  // =========================================================
  // ORDER BY START TIME
  // =========================================================

  @Override
  public Mono<PageResult<CourseScheduleDto>> findOrderedByStartTime(
      boolean ascending,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .from(page * size)
        .size(size)
        .sort(sort -> sort
            .field(field -> field
                .field("startTime")
                .order(
                    ascending
                        ? co.elastic.clients.elasticsearch._types.SortOrder.Asc
                        : co.elastic.clients.elasticsearch._types.SortOrder.Desc))));

    return executeSearch(
        request,
        page,
        size);
  }

  // =========================================================
  // STATISTICS
  // =========================================================

  @Override
  public Mono<CourseScheduleStatisticsDto> statistics() {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .size(0)

        .aggregations(
            "coursesWithSchedule",
            a -> a.cardinality(
                c -> c
                    .field("courseId")))

        .aggregations(
            "totalScheduledMinutes",
            a -> a.sum(
                sum -> sum
                    .field("durationMinutes"))));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(
            Schedulers.boundedElastic())
        .map(response -> {

          long total = response.hits().total() != null
              ? response.hits()
                  .total()
                  .value()
              : 0L;

          Map<String, Aggregate> aggregations = response.aggregations();

          long coursesWithSchedule = 0L;

          Aggregate courses = aggregations.get(
              "coursesWithSchedule");

          if (courses != null
              && courses.isCardinality()) {

            coursesWithSchedule = courses.cardinality()
                .value();
          }

          long totalScheduledMinutes = 0L;

          Aggregate minutes = aggregations.get(
              "totalScheduledMinutes");

          if (minutes != null
              && minutes.isSum()) {

            totalScheduledMinutes = Math.round(
                minutes.sum().value());
          }

          long totalScheduledHours = totalScheduledMinutes / 60L;

          return new CourseScheduleStatisticsDto(
              total,
              coursesWithSchedule,
              totalScheduledHours);
        });
  }

  // =========================================================
  // COUNT
  // =========================================================

  @Override
  public Mono<Long> count() {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .size(0));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .map(response -> response.hits().total() != null
            ? response.hits()
                .total()
                .value()
            : 0L);
  }

  // =========================================================
  // COUNT BY COURSE
  // =========================================================

  @Override
  public Mono<Long> countByCourseId(
      UUID courseId) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q
            .term(t -> t
                .field("courseId")
                .value(courseId.toString())))
        .size(0));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .map(response -> response.hits().total() != null
            ? response.hits()
                .total()
                .value()
            : 0L);
  }

  // =========================================================
  // INDEX
  // =========================================================

  @Override
  public Mono<Void> index(
      CourseScheduleDto schedule) {

    return Mono.fromFuture(
        client.index(i -> i
            .index(INDEX)
            .id(schedule.id().toString())
            .document(
                toDocument(schedule))))
        .then();
  }

  // =========================================================
  // BULK INDEX
  // =========================================================

  @Override
  public Mono<Void> bulkIndex(
      List<CourseScheduleDto> schedules) {

    if (schedules == null
        || schedules.isEmpty()) {

      return Mono.empty();
    }

    List<BulkOperation> operations = schedules.stream()
        .map(schedule -> BulkOperation.of(
            operation -> operation
                .index(index -> index
                    .index(INDEX)
                    .id(schedule.id().toString())
                    .document(
                        toDocument(
                            schedule)))))
        .collect(
            Collectors.toList());

    return Mono.fromFuture(
        client.bulk(
            BulkRequest.of(
                request -> request.operations(
                    operations))))
        .then();
  }

  // =========================================================
  // DELETE
  // =========================================================

  @Override
  public Mono<Void> deleteFromIndex(
      UUID scheduleId) {

    return Mono.fromFuture(
        client.delete(
            DeleteRequest.of(
                request -> request
                    .index(INDEX)
                    .id(
                        scheduleId.toString()))))
        .then();
  }

  // =========================================================
  // EXECUTE SEARCH
  // =========================================================

  private Mono<PageResult<CourseScheduleDto>> executeSearch(
      SearchRequest request,
      int page,
      int size) {

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(
            Schedulers.boundedElastic())
        .map(response -> {

          List<CourseScheduleDto> content = response.hits()
              .hits()
              .stream()
              .map(Hit::source)
              .map(this::asMap)
              .map(this::toDto)
              .collect(Collectors.toList());

          long total = response.hits().total() != null
              ? response.hits()
                  .total()
                  .value()
              : content.size();

          return PageResult.of(
              content,
              total,
              page,
              size);
        });
  }

  // =========================================================
  // MAP
  // =========================================================

  @SuppressWarnings("unchecked")
  private Map<String, Object> asMap(
      Object source) {

    return (Map<String, Object>) source;
  }

  // =========================================================
  // DOCUMENT
  // =========================================================

  private Map<String, Object> toDocument(
      CourseScheduleDto schedule) {

    Map<String, Object> document = new HashMap<>();

    document.put(
        "id",
        schedule.id().toString());

    document.put(
        "courseId",
        schedule.courseId() != null
            ? schedule.courseId().toString()
            : null);

    document.put(
        "dayOfWeek",
        schedule.dayOfWeek() != null
            ? schedule.dayOfWeek().name()
            : null);

    document.put(
        "startTime",
        schedule.startTime() != null
            ? schedule.startTime().toString()
            : null);

    document.put(
        "endTime",
        schedule.endTime() != null
            ? schedule.endTime().toString()
            : null);

    document.put(
        "room",
        schedule.room());

    document.put(
        "durationMinutes",
        calculateDurationMinutes(
            schedule.startTime(),
            schedule.endTime()));

    return document;
  }

  // =========================================================
  // DTO
  // =========================================================

  private CourseScheduleDto toDto(
      Map<String, Object> document) {

    return new CourseScheduleDto(

        UUID.fromString(
            document.get("id").toString()),

        document.get("courseId") != null
            ? UUID.fromString(
                document.get("courseId").toString())
            : null,

        document.get("dayOfWeek") != null
            ? DayOfWeek.valueOf(
                document
                    .get("dayOfWeek")
                    .toString())
            : null,

        parseLocalTime(
            document.get("startTime")),

        parseLocalTime(
            document.get("endTime")),

        document.get("room") != null
            ? document
                .get("room")
                .toString()
            : null);
  }

  // =========================================================
  // SUGGESTION
  // =========================================================

  private CourseScheduleSuggestionDto toSuggestion(
      Map<String, Object> document) {

    return new CourseScheduleSuggestionDto(

        UUID.fromString(
            document.get("id").toString()),

        document.get("courseId") != null
            ? UUID.fromString(
                document
                    .get("courseId")
                    .toString())
            : null,

        document.get("dayOfWeek") != null
            ? document
                .get("dayOfWeek")
                .toString()
            : null,

        parseLocalTime(
            document.get("startTime")),

        parseLocalTime(
            document.get("endTime")));
  }

  // =========================================================
  // LOCAL TIME
  // =========================================================

  private LocalTime parseLocalTime(
      Object value) {

    if (value == null) {
      return null;
    }

    return LocalTime.parse(
        value.toString());
  }

  // =========================================================
  // DURATION
  // =========================================================

  private long calculateDurationMinutes(
      LocalTime start,
      LocalTime end) {

    if (start == null
        || end == null) {

      return 0L;
    }

    return Duration
        .between(start, end)
        .toMinutes();
  }
}
