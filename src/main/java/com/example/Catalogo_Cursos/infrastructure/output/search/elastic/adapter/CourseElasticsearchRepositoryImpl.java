package com.example.Catalogo_Cursos.infrastructure.output.search.elastic.adapter;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.json.JsonData;
import java.time.Instant;
import com.example.Catalogo_Cursos.application.dto.course.*;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.infrastructure.output.search.elastic.repository.CourseElasticsearchRepository;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class CourseElasticsearchRepositoryImpl
    implements CourseElasticsearchRepository {

  private static final String INDEX = "courses";

  private static final String STATUS_PUBLISHED = "PUBLISHED";
  private static final String STATUS_DRAFT = "DRAFT";
  private static final String STATUS_CANCELLED = "CANCELLED";
  private static final String STATUS_COMPLETED = "COMPLETED";

  private final ElasticsearchAsyncClient client;

  public CourseElasticsearchRepositoryImpl(
      ElasticsearchAsyncClient client) {

    this.client = client;
  }

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  @Override
  public Mono<CourseDto> findById(UUID id) {

    return Mono.fromFuture(
        client.get(g -> g
            .index(INDEX)
            .id(id.toString()),
            Map.class))
        .publishOn(Schedulers.boundedElastic())
        .mapNotNull(response -> response.found()
            ? toDto(
                asStringObjectMap(
                    response.source()))
            : null);
  }

  @Override
  public Mono<CourseDto> findByCode(String code) {

    return findSingleByTerm(
        "code",
        code);
  }

  // =========================================================
  // BASIC SEARCH
  // =========================================================

  @Override
  public Mono<PageResult<CourseSummaryDto>> search(
      String text,
      int page,
      int size) {

    Query query = Query.of(q -> q
        .multiMatch(mm -> mm
            .query(text)
            .fields(
                "name^3",
                "code^2",
                "description")
            .fuzziness("AUTO")));

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(query)
        .from(page * size)
        .size(size));

    return executeSummarySearch(
        request,
        page,
        size);
  }

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

    BoolQuery.Builder bool = new BoolQuery.Builder();

    if (name != null && !name.isBlank()) {

      bool.must(m -> m.match(
          mt -> mt
              .field("name")
              .query(name)));
    }

    if (code != null && !code.isBlank()) {

      bool.filter(f -> f.term(
          t -> t
              .field("code")
              .value(code)));
    }

    if (modality != null && !modality.isBlank()) {

      bool.filter(f -> f.term(
          t -> t
              .field("modality")
              .value(modality)));
    }

    if (status != null && !status.isBlank()) {

      bool.filter(f -> f.term(
          t -> t
              .field("status")
              .value(status)));
    }

    if (startDateAfter != null ||
        startDateBefore != null) {

      bool.filter(f -> f.range(
          r -> {

            r.field("startDate");

            if (startDateAfter != null) {

              r.gte(JsonData.of(
                  startDateAfter.toString()));
            }

            if (startDateBefore != null) {

              r.lte(JsonData.of(
                  startDateBefore.toString()));
            }

            return r;
          }));
    }

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.bool(
            bool.build()))
        .from(page * size)
        .size(size));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  // =========================================================
  // FILTERS
  // =========================================================

  @Override
  public Mono<PageResult<CourseSummaryDto>> findByCategory(
      UUID categoryId,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.term(
            t -> t
                .field("categories")
                .value(categoryId.toString())))
        .from(page * size)
        .size(size));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findByCategories(
      Set<UUID> categoryIds,
      int page,
      int size) {

    List<FieldValue> values = categoryIds.stream()
        .map(UUID::toString)
        .map(FieldValue::of)
        .collect(Collectors.toList());

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.terms(
            t -> t
                .field("categories")
                .terms(tv -> tv.value(values))))
        .from(page * size)
        .size(size));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findByModality(
      String modality,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.term(
            t -> t
                .field("modality")
                .value(modality)))
        .from(page * size)
        .size(size));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findByStatus(
      String status,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.term(
            t -> t
                .field("status")
                .value(status)))
        .from(page * size)
        .size(size));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findPublished(
      int page,
      int size) {

    return findByStatus(
        STATUS_PUBLISHED,
        page,
        size);
  }

  // =========================================================
  // DATE FILTERS
  // =========================================================

  @Override
  public Mono<PageResult<CourseSummaryDto>> findUpcoming(
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.bool(
            b -> b
                .filter(f -> f.term(
                    t -> t
                        .field("status")
                        .value(STATUS_PUBLISHED)))
                .filter(f -> f.range(
                    r -> r
                        .field("startDate")
                        .gte(JsonData.of(
                            LocalDate.now()
                                .toString()))))))
        .from(page * size)
        .size(size)
        .sort(so -> so.field(
            f -> f
                .field("startDate")
                .order(SortOrder.Asc))));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findByStartDateRange(
      LocalDate from,
      LocalDate to,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.range(
            r -> r
                .field("startDate")
                .gte(JsonData.of(
                    from.toString()))
                .lte(JsonData.of(
                    to.toString()))))
        .from(page * size)
        .size(size)
        .sort(so -> so.field(
            f -> f
                .field("startDate")
                .order(SortOrder.Asc))));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findByEndDateRange(
      LocalDate from,
      LocalDate to,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.range(
            r -> r
                .field("endDate")
                .gte(JsonData.of(
                    from.toString()))
                .lte(JsonData.of(
                    to.toString()))))
        .from(page * size)
        .size(size)
        .sort(so -> so.field(
            f -> f
                .field("endDate")
                .order(SortOrder.Asc))));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  // =========================================================
  // CAPACITY
  // =========================================================

  @Override
  public Mono<PageResult<CourseSummaryDto>> findWithAvailableSlots(
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.range(
            r -> r
                .field("availableSlots")
                .gt(JsonData.of(0))))
        .from(page * size)
        .size(size));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findFull(
      int page,
      int size) {

    /*
     * Elasticsearch no permite comparar dos campos
     * directamente con un range normal.
     *
     * Por eso se utiliza una script query.
     */
    Query query = Query.of(q -> q.script(
        sc -> sc.script(
            s -> s.inline(
                i -> i.source(
                    "doc['availableSlots'].size() > 0 && " +
                        "doc['capacity'].size() > 0 && " +
                        "doc['availableSlots'].value == " +
                        "doc['capacity'].value")))));

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(query)
        .from(page * size)
        .size(size));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  // =========================================================
  // PRICE
  // =========================================================

  @Override
  public Mono<PageResult<CourseSummaryDto>> findByPriceRange(
      BigDecimal minPrice,
      BigDecimal maxPrice,
      String currency,
      int page,
      int size) {

    BoolQuery.Builder bool = new BoolQuery.Builder();

    if (currency != null &&
        !currency.isBlank()) {

      bool.filter(f -> f.term(
          t -> t
              .field("currency")
              .value(currency)));
    }

    if (minPrice != null ||
        maxPrice != null) {

      bool.filter(f -> f.range(
          r -> {

            r.field("price");

            if (minPrice != null) {

              r.gte(
                  JsonData.of(minPrice));
            }

            if (maxPrice != null) {

              r.lte(
                  JsonData.of(maxPrice));
            }

            return r;
          }));
    }

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.bool(
            bool.build()))
        .from(page * size)
        .size(size));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  // =========================================================
  // ORDERING
  // =========================================================

  @Override
  public Mono<PageResult<CourseSummaryDto>> findOrderedByName(
      boolean ascending,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .from(page * size)
        .size(size)
        .sort(so -> so.field(
            f -> f
                .field("name.keyword")
                .order(
                    ascending
                        ? SortOrder.Asc
                        : SortOrder.Desc))));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findOrderedByStartDate(
      boolean ascending,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .from(page * size)
        .size(size)
        .sort(so -> so.field(
            f -> f
                .field("startDate")
                .order(
                    ascending
                        ? SortOrder.Asc
                        : SortOrder.Desc))));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseSummaryDto>> findOrderedByPrice(
      boolean ascending,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .from(page * size)
        .size(size)
        .sort(so -> so.field(
            f -> f
                .field("price")
                .order(
                    ascending
                        ? SortOrder.Asc
                        : SortOrder.Desc))));

    return executeSummarySearch(
        request,
        page,
        size);
  }

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  @Override
  public Mono<List<CourseSuggestionDto>> autocomplete(
      String text,
      int limit) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .suggest(su -> su
            .suggesters(
                "course-suggest",
                fs -> fs
                    .prefix(text)
                    .completion(
                        c -> c
                            .field("name.suggest")
                            .skipDuplicates(true)
                            .size(limit)))));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(
            Schedulers.boundedElastic())
        .map(response -> {

          var suggestions = response.suggest()
              .get("course-suggest");

          if (suggestions == null) {
            return List.<CourseSuggestionDto>of();
          }

          return suggestions.stream()
              .filter(entry -> entry.completion() != null)
              .flatMap(entry -> entry
                  .completion()
                  .options()
                  .stream())
              .map(option -> {

                Map<String, Object> source = asStringObjectMap(
                    option.source());

                Object idValue = source.get("courseId");

                if (idValue == null) {
                  idValue = source.get("id");
                }

                if (idValue == null) {
                  throw new IllegalStateException(
                      "La sugerencia de Elasticsearch no contiene " +
                          "'courseId' ni 'id': " + source);
                }

                return new CourseSuggestionDto(
                    UUID.fromString(idValue.toString()),
                    (String) source.get("code"),
                    (String) source.get("name"));
              })
              .collect(Collectors.toList());
        });
  }

  // =========================================================
  // STATISTICS
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

  @Override
  public Mono<CourseStatisticsDto> statistics() {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .size(0)

        // =====================================================
        // STATUS
        // =====================================================

        .aggregations(
            "draftCourses",
            a -> a.filter(
                f -> f.term(
                    t -> t
                        .field("status")
                        .value(STATUS_DRAFT))))

        .aggregations(
            "publishedCourses",
            a -> a.filter(
                f -> f.term(
                    t -> t
                        .field("status")
                        .value(STATUS_PUBLISHED))))

        .aggregations(
            "cancelledCourses",
            a -> a.filter(
                f -> f.term(
                    t -> t
                        .field("status")
                        .value(STATUS_CANCELLED))))

        .aggregations(
            "completedCourses",
            a -> a.filter(
                f -> f.term(
                    t -> t
                        .field("status")
                        .value(STATUS_COMPLETED))))

        // =====================================================
        // AVAILABLE COURSES
        //
        // availableSlots > 0
        // =====================================================

        .aggregations(
            "availableCourses",
            a -> a.filter(
                f -> f.range(
                    r -> r
                        .field("availableSlots")
                        .gt(JsonData.of(0)))))

        // =====================================================
        // FULL COURSES
        //
        // availableSlots <= 0
        // =====================================================

        .aggregations(
            "fullCourses",
            a -> a.filter(
                f -> f.range(
                    r -> r
                        .field("availableSlots")
                        .lte(JsonData.of(0)))))

        // =====================================================
        // PRICE
        // =====================================================

        .aggregations(
            "averagePrice",
            a -> a.avg(
                avg -> avg
                    .field("price")))

        .aggregations(
            "minimumPrice",
            a -> a.min(
                min -> min
                    .field("price")))

        .aggregations(
            "maximumPrice",
            a -> a.max(
                max -> max
                    .field("price"))));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(Schedulers.boundedElastic())
        .map(response -> {

          // ===================================================
          // TOTAL
          // ===================================================

          long totalCourses = response.hits().total() != null
              ? response.hits()
                  .total()
                  .value()
              : 0L;

          Map<String, Aggregate> aggs = response.aggregations();

          // ===================================================
          // STATUS COUNTS
          // ===================================================

          long draftCourses = aggs.get("draftCourses")
              .filter()
              .docCount();

          long publishedCourses = aggs.get("publishedCourses")
              .filter()
              .docCount();

          long cancelledCourses = aggs.get("cancelledCourses")
              .filter()
              .docCount();

          long completedCourses = aggs.get("completedCourses")
              .filter()
              .docCount();

          // ===================================================
          // CAPACITY
          // ===================================================

          long availableCourses = aggs.get("availableCourses")
              .filter()
              .docCount();

          long fullCourses = aggs.get("fullCourses")
              .filter()
              .docCount();

          // ===================================================
          // PRICE
          // ===================================================

          BigDecimal averagePrice = parseAggregationPrice(
              aggs.get("averagePrice"));

          BigDecimal minimumPrice = parseAggregationPrice(
              aggs.get("minimumPrice"));

          BigDecimal maximumPrice = parseAggregationPrice(
              aggs.get("maximumPrice"));

          // ===================================================
          // DTO
          // ===================================================

          return new CourseStatisticsDto(
              totalCourses,
              draftCourses,
              publishedCourses,
              cancelledCourses,
              completedCourses,
              availableCourses,
              fullCourses,
              averagePrice,
              minimumPrice,
              maximumPrice);
        });
  }

  // =========================================================
  // LIST
  // =========================================================

  @Override
  public Flux<CourseDto> findAll() {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .size(10_000));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(
            Schedulers.boundedElastic())
        .flatMapMany(response -> Flux.fromStream(
            response
                .hits()
                .hits()
                .stream()
                .map(Hit::source)
                .map(this::asStringObjectMap)
                .map(this::toDto)));
  }

  // =========================================================
  // INDEXACIÓN
  // =========================================================

  @Override
  public Mono<Void> index(
      CourseDto course) {

    return Mono.fromFuture(
        client.index(i -> i
            .index(INDEX)
            .id(course.id().toString())
            .document(
                toDocument(course))))
        .then();
  }

  @Override
  public Mono<Void> bulkIndex(
      List<CourseDto> courses) {

    List<BulkOperation> operations = courses.stream()
        .map(course -> BulkOperation.of(
            bo -> bo.index(
                idx -> idx
                    .index(INDEX)
                    .id(course.id().toString())
                    .document(
                        toDocument(
                            course)))))
        .collect(Collectors.toList());

    return Mono.fromFuture(
        client.bulk(
            BulkRequest.of(
                b -> b.operations(
                    operations))))
        .then();
  }

  @Override
  public Mono<Void> deleteFromIndex(
      UUID courseId) {

    return Mono.fromFuture(
        client.delete(
            DeleteRequest.of(
                d -> d
                    .index(INDEX)
                    .id(courseId.toString()))))
        .then();
  }

  // =========================================================
  // HELPERS
  // =========================================================

  private Mono<CourseDto> findSingleByTerm(
      String field,
      String value) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.term(
            t -> t
                .field(field)
                .value(value)))
        .size(1));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(
            Schedulers.boundedElastic())
        .mapNotNull(response -> response.hits()
            .hits()
            .stream()
            .findFirst()
            .map(Hit::source)
            .map(this::asStringObjectMap)
            .map(this::toDto)
            .orElse(null));
  }

  private Mono<PageResult<CourseSummaryDto>> executeSummarySearch(
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

          List<CourseSummaryDto> items = response.hits()
              .hits()
              .stream()
              .map(Hit::source)
              .map(this::asStringObjectMap)
              .map(this::toSummaryDto)
              .collect(Collectors.toList());

          long total = response.hits().total() != null
              ? response.hits()
                  .total()
                  .value()
              : items.size();

          return PageResult.of(
              items,
              total,
              page,
              size);
        });
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> asStringObjectMap(
      Object source) {

    if (source == null) {
      return Map.of();
    }

    if (!(source instanceof Map<?, ?>)) {
      throw new IllegalStateException(
          "El source de Elasticsearch no es un objeto JSON: " +
              source.getClass().getName());
    }

    return (Map<String, Object>) source;
  }

  private BigDecimal parseAggregationPrice(
      Aggregate aggregate) {

    if (aggregate == null) {
      return null;
    }

    if (aggregate.avg() != null) {
      Double value = aggregate.avg().value();

      return value != null && !value.isNaN()
          ? BigDecimal.valueOf(value)
          : null;
    }

    if (aggregate.min() != null) {
      Double value = aggregate.min().value();

      return value != null && !value.isNaN()
          ? BigDecimal.valueOf(value)
          : null;
    }

    if (aggregate.max() != null) {
      Double value = aggregate.max().value();

      return value != null && !value.isNaN()
          ? BigDecimal.valueOf(value)
          : null;
    }

    return null;
  }

  // =========================================================
  // DTO MAPPING
  // =========================================================

  private CourseDto toDto(
      Map<String, Object> doc) {

    return new CourseDto(

        UUID.fromString(
            doc.get("id").toString()),

        (String) doc.get("code"),

        (String) doc.get("name"),

        (String) doc.get("description"),

        (String) doc.get("modality"),

        parseBigDecimal(
            doc.get("price")),

        (String) doc.get("currency"),

        parseDate(
            doc.get("startDate")),

        parseDate(
            doc.get("endDate")),

        parseTime(
            doc.get("startTime")),

        parseInteger(
            doc.get("durationHours")),

        parseInteger(
            doc.get("capacity")),

        parseInteger(
            doc.get("availableSlots")),

        (String) doc.get("status"),

        /*
         * Elasticsearch no contiene aquí necesariamente
         * los DTO completos de schedules.
         *
         * Si quieres que findById() devuelva las relaciones
         * completas, tendrás que indexarlas o hacer una consulta
         * adicional.
         */
        List.of(),

        List.of(),

        List.of(),

        parseInstant(
            doc.get("createdAt")),

        parseInstant(
            doc.get("updatedAt")));
  }

  private CourseSummaryDto toSummaryDto(
      Map<String, Object> doc) {

    return new CourseSummaryDto(

        UUID.fromString(
            doc.get("id").toString()),

        (String) doc.get("code"),

        (String) doc.get("name"),

        (String) doc.get("modality"),

        parseBigDecimal(
            doc.get("price")),

        (String) doc.get("currency"),

        parseDate(
            doc.get("startDate")),

        parseDate(
            doc.get("endDate")),

        parseTime(
            doc.get("startTime")),

        parseInteger(
            doc.get("durationHours")),

        parseInteger(
            doc.get("capacity")),

        parseInteger(
            doc.get("availableSlots")),

        (String) doc.get("status"));
  }

  // =========================================================
  // DOCUMENT
  // =========================================================

  private Map<String, Object> toDocument(
      CourseDto course) {

    Map<String, Object> doc = new HashMap<>();

    doc.put(
        "id",
        course.id().toString());

    doc.put(
        "code",
        course.code());

    doc.put(
        "name",
        course.name());

    doc.put(
        "description",
        course.description());

    doc.put(
        "modality",
        course.modality());

    if (course.price() != null) {

      doc.put(
          "price",
          course.price());
    }

    doc.put(
        "currency",
        course.currency());

    if (course.startDate() != null) {

      doc.put(
          "startDate",
          course.startDate().toString());
    }

    if (course.endDate() != null) {

      doc.put(
          "endDate",
          course.endDate().toString());
    }

    if (course.startTime() != null) {

      doc.put(
          "startTime",
          course.startTime().toString());
    }

    doc.put(
        "durationHours",
        course.durationHours());

    doc.put(
        "capacity",
        course.capacity());

    doc.put(
        "availableSlots",
        course.availableSlots());

    doc.put(
        "status",
        course.status());

    // =======================================================
    // RELACIONES
    // =======================================================

    if (course.categories() != null) {

      doc.put(
          "categories",
          course.categories()
              .stream()
              .map(category -> category.id().toString())
              .collect(Collectors.toList()));
    }

    if (course.schedules() != null) {

      doc.put(
          "schedules",
          course.schedules());
    }

    if (course.locations() != null) {

      doc.put(
          "locations",
          course.locations());
    }

    // =======================================================
    // AUDITORÍA
    // =======================================================

    if (course.createdAt() != null) {

      doc.put(
          "createdAt",
          course.createdAt().toString());
    }

    if (course.updatedAt() != null) {

      doc.put(
          "updatedAt",
          course.updatedAt().toString());
    }

    return doc;
  }

  // =========================================================
  // PARSERS
  // =========================================================

  private LocalDate parseDate(
      Object value) {

    if (value == null) {
      return null;
    }

    if (value instanceof List<?> values &&
        values.size() == 3) {

      return LocalDate.of(
          Integer.parseInt(
              values.get(0).toString()),
          Integer.parseInt(
              values.get(1).toString()),
          Integer.parseInt(
              values.get(2).toString()));
    }

    return LocalDate.parse(
        value.toString());
  }

  private LocalTime parseTime(
      Object value) {

    if (value == null) {
      return null;
    }

    if (value instanceof LocalTime time) {
      return time;
    }

    if (value instanceof List<?> values &&
        values.size() >= 2) {

      int hour = Integer.parseInt(
          values.get(0).toString());

      int minute = Integer.parseInt(
          values.get(1).toString());

      int second = values.size() >= 3
          ? Integer.parseInt(
              values.get(2).toString())
          : 0;

      int nano = values.size() >= 4
          ? Integer.parseInt(
              values.get(3).toString())
          : 0;

      return LocalTime.of(
          hour,
          minute,
          second,
          nano);
    }

    return LocalTime.parse(
        value.toString());
  }

  private Integer parseInteger(
      Object value) {

    if (value == null) {
      return null;
    }

    if (value instanceof Number number) {
      return number.intValue();
    }

    return Integer.valueOf(value.toString());
  }

  private BigDecimal parseBigDecimal(
      Object value) {

    if (value == null) {
      return null;
    }

    if (value instanceof BigDecimal decimal) {
      return decimal;
    }

    return new BigDecimal(
        value.toString());
  }

  private Instant parseInstant(
      Object value) {

    if (value == null) {
      return null;
    }

    if (value instanceof Instant instant) {
      return instant;
    }

    if (value instanceof Number number) {

      long epoch = number.longValue();

      /*
       * Soporta epoch_seconds y epoch_millis sin usar double,
       * evitando pérdida de precisión.
       */
      if (Math.abs(epoch) >= 100_000_000_000L) {
        return Instant.ofEpochMilli(epoch);
      }

      return Instant.ofEpochSecond(epoch);
    }

    String text = value.toString().trim();

    if (text.isEmpty()) {
      return null;
    }

    return Instant.parse(text);
  }
}
