package com.example.Catalogo_Cursos.infrastructure.output.search.elastic.adapter;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryStatisticsDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.infrastructure.output.search.elastic.repository.CourseCategoryElasticsearchRepository;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class CourseCategoryElasticsearchRepositoryImpl
    implements CourseCategoryElasticsearchRepository {

  // =========================================================
  // INDEX
  // =========================================================

  private static final String INDEX = "course_categories";

  // =========================================================
  // FIELDS
  // =========================================================

  private static final String FIELD_ID = "courseCategoryId";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_STATUS = "status";
  private static final String FIELD_DESCRIPTION = "description";
  private static final String FIELD_COURSE_IDS = "courseIds";

  // =========================================================
  // STATUS
  // =========================================================

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";

  // =========================================================
  // DEPENDENCY
  // =========================================================

  private final ElasticsearchAsyncClient client;

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  public CourseCategoryElasticsearchRepositoryImpl(
      ElasticsearchAsyncClient client) {

    this.client = client;
  }

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  @Override
  public Mono<CourseCategoryDto> findById(
      UUID categoryId) {

    return findSingleByTerm(
        FIELD_ID,
        categoryId.toString());
  }

  @Override
  public Mono<CourseCategoryDto> findByName(
      String name) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.term(
            t -> t
                .field(FIELD_NAME + ".keyword")
                .value(name)))
        .size(1));

    return executeSingleSearch(request);
  }

  // =========================================================
  // SEARCH
  // =========================================================

  @Override
  public Mono<PageResult<CourseCategoryDto>> search(
      String text,
      int page,
      int size) {

    if (text == null || text.isBlank()) {
      return findAll(page, size);
    }

    Query query = Query.of(q -> q
        .multiMatch(mm -> mm
            .query(text)
            .fields(
                FIELD_NAME + "^3",
                FIELD_DESCRIPTION)
            .fuzziness("AUTO")));

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(query)
        .from(page * size)
        .size(size));

    return executeSearch(
        request,
        page,
        size);
  }

  @Override
  public Mono<PageResult<CourseCategoryDto>> searchByCriteria(
      String name,
      String status,
      int page,
      int size) {

    BoolQuery.Builder bool = new BoolQuery.Builder();

    if (name != null && !name.isBlank()) {

      bool.must(
          m -> m.match(
              mt -> mt
                  .field(FIELD_NAME)
                  .query(name)));
    }

    if (status != null && !status.isBlank()) {

      bool.filter(
          f -> f.term(
              t -> t
                  .field(FIELD_STATUS)
                  .value(status)));
    }

    Query query = Query.of(
        q -> q.bool(bool.build()));

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(query)
        .from(page * size)
        .size(size));

    return executeSearch(
        request,
        page,
        size);
  }

  // =========================================================
  // STATUS
  // =========================================================

  @Override
  public Mono<PageResult<CourseCategoryDto>> findByStatus(
      String status,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.term(
            t -> t
                .field(FIELD_STATUS)
                .value(status)))
        .from(page * size)
        .size(size));

    return executeSearch(
        request,
        page,
        size);
  }

  // =========================================================
  // COURSE RELATION
  // =========================================================

  @Override
  public Mono<PageResult<CourseCategoryDto>> findByCourseId(
      UUID courseId,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.term(
            t -> t
                .field(FIELD_COURSE_IDS)
                .value(courseId.toString())))
        .from(page * size)
        .size(size));

    return executeSearch(
        request,
        page,
        size);
  }

  @Override
  public Flux<CourseCategoryDto> findAllByCourseId(
      UUID courseId) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.term(
            t -> t
                .field(FIELD_COURSE_IDS)
                .value(courseId.toString())))
        .size(10_000));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))

        .publishOn(Schedulers.boundedElastic())

        .flatMapMany(
            response -> Flux.fromStream(
                response
                    .hits()
                    .hits()
                    .stream()
                    .map(Hit::source)
                    .map(this::asStringObjectMap)
                    .map(this::toDto)));
  }

  // =========================================================
  // CATEGORIES WITH COURSES
  // =========================================================

  @Override
  public Mono<Long> countCoursesByCategoryId(
      UUID categoryId) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .size(0)
        .query(q -> q.term(
            t -> t
                .field(FIELD_ID)
                .value(categoryId.toString())))
        .aggregations(
            "course_count",
            a -> a.sum(
                sum -> sum.script(
                    script -> script.inline(
                        inline -> inline.source(
                            "doc['"
                                + FIELD_COURSE_IDS
                                + "'].size()"))))));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))

        .publishOn(Schedulers.boundedElastic())

        .map(response -> {

          Aggregate aggregate = response
              .aggregations()
              .get("course_count");

          if (aggregate == null ||
              aggregate.sum() == null) {

            return 0L;
          }

          return (long) aggregate
              .sum()
              .value();
        });
  }

  // =========================================================
  // ORDERING
  // =========================================================

  @Override
  public Mono<PageResult<CourseCategoryDto>> findOrderedByName(
      boolean ascending,
      int page,
      int size) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .from(page * size)
        .size(size)
        .sort(so -> so.field(
            f -> f
                .field(FIELD_NAME + ".keyword")
                .order(
                    ascending
                        ? SortOrder.Asc
                        : SortOrder.Desc))));

    return executeSearch(
        request,
        page,
        size);
  }

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  @Override
  public Mono<List<CourseCategoryDto>> autocomplete(
      String text,
      int limit) {

    if (text == null || text.isBlank()) {
      return Mono.just(List.of());
    }

    Query query = Query.of(q -> q
        .matchPhrasePrefix(
            m -> m
                .field(FIELD_NAME)
                .query(text)));

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(query)
        .size(limit)
        .sort(so -> so.field(
            f -> f
                .field(FIELD_NAME + ".keyword")
                .order(SortOrder.Asc))));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))

        .publishOn(Schedulers.boundedElastic())

        .map(response -> response
            .hits()
            .hits()
            .stream()
            .map(Hit::source)
            .map(this::asStringObjectMap)
            .map(this::toDto)
            .collect(Collectors.toList()));
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
            ? response.hits().total().value()
            : 0L);
  }

  @Override
  public Mono<Long> countActive() {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.term(
            t -> t
                .field(FIELD_STATUS)
                .value(STATUS_ACTIVE)))
        .size(0));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))

        .map(response -> response.hits().total() != null
            ? response.hits().total().value()
            : 0L);
  }

  @Override
  public Mono<CourseCategoryStatisticsDto> statistics() {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .size(0)

        .aggregations(
            "active",
            a -> a.filter(
                f -> f.term(
                    t -> t
                        .field(FIELD_STATUS)
                        .value(STATUS_ACTIVE))))

        .aggregations(
            "inactive",
            a -> a.filter(
                f -> f.term(
                    t -> t
                        .field(FIELD_STATUS)
                        .value(STATUS_INACTIVE)))));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))

        .publishOn(Schedulers.boundedElastic())

        .map(response -> {

          long total = response.hits().total() != null
              ? response.hits().total().value()
              : 0L;

          Map<String, Aggregate> aggs = response.aggregations();

          long active = aggs.containsKey("active")
              ? aggs.get("active")
                  .filter()
                  .docCount()
              : 0L;

          long inactive = aggs.containsKey("inactive")
              ? aggs.get("inactive")
                  .filter()
                  .docCount()
              : 0L;

          return new CourseCategoryStatisticsDto(
              total,
              active,
              inactive);
        });
  }

  // =========================================================
  // ADMIN
  // =========================================================

  @Override
  public Mono<PageResult<CourseCategoryDto>> findAll(
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
  // SEARCH EXECUTION
  // =========================================================

  private Mono<PageResult<CourseCategoryDto>> executeSearch(
      SearchRequest request,
      int page,
      int size) {

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))

        .publishOn(Schedulers.boundedElastic())

        .map(response -> {

          List<CourseCategoryDto> items = response
              .hits()
              .hits()
              .stream()
              .map(Hit::source)
              .map(this::asStringObjectMap)
              .map(this::toDto)
              .collect(Collectors.toList());

          long total = response.hits().total() != null
              ? response.hits().total().value()
              : items.size();

          return PageResult.of(
              items,
              total,
              page,
              size);
        });
  }

  // =========================================================
  // SINGLE SEARCH
  // =========================================================

  private Mono<CourseCategoryDto> executeSingleSearch(
      SearchRequest request) {

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))

        .publishOn(Schedulers.boundedElastic())

        .flatMap(response -> {

          if (response.hits().hits().isEmpty()) {
            return Mono.empty();
          }

          return Mono.just(
              response
                  .hits()
                  .hits()
                  .stream()
                  .findFirst()
                  .map(Hit::source)
                  .map(this::asStringObjectMap)
                  .map(this::toDto)
                  .orElseThrow());
        });
  }

  // =========================================================
  // FIND SINGLE BY TERM
  // =========================================================

  private Mono<CourseCategoryDto> findSingleByTerm(
      String field,
      String value) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q.term(
            t -> t
                .field(field)
                .value(value)))
        .size(1));

    return executeSingleSearch(request);
  }

  // =========================================================
  // MAP
  // =========================================================

  @SuppressWarnings("unchecked")
  private Map<String, Object> asStringObjectMap(
      Object source) {

    if (!(source instanceof Map<?, ?>)) {
      throw new IllegalStateException(
          "El source de Elasticsearch no es un Map: "
              + source);
    }

    return (Map<String, Object>) source;
  }

  // =========================================================
  // DTO MAPPING
  // =========================================================

  private CourseCategoryDto toDto(
      Map<String, Object> doc) {

    Object categoryId = doc.get(FIELD_ID);

    if (categoryId == null) {
      throw new IllegalStateException(
          "El documento de Elasticsearch no contiene '"
              + FIELD_ID
              + "'. Documento: "
              + doc);
    }

    return new CourseCategoryDto(

        UUID.fromString(
            categoryId.toString()),

        (String) doc.get(FIELD_NAME),

        (String) doc.get(FIELD_DESCRIPTION),

        (String) doc.get(FIELD_STATUS));
  }
}
