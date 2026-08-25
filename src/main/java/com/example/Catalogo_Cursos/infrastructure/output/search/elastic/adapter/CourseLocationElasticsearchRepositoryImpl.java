package com.example.Catalogo_Cursos.infrastructure.output.search.elastic.adapter;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;

import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationDto;
import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationStatisticsDto;
import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationSuggestionDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.infrastructure.output.search.elastic.repository.CourseLocationElasticsearchRepository;

import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class CourseLocationElasticsearchRepositoryImpl
    implements CourseLocationElasticsearchRepository {

  private static final String INDEX = "course_locations";

  private final ElasticsearchAsyncClient client;

  public CourseLocationElasticsearchRepositoryImpl(
      ElasticsearchAsyncClient client) {

    this.client = client;
  }

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  @Override
  public Mono<CourseLocationDto> findById(
      UUID locationId) {

    return findSingleByTerm(
        "id",
        locationId.toString());
  }

  @Override
  public Mono<CourseLocationDto> findByCourseId(
      UUID courseId) {

    return findSingleByTerm(
        "courseId",
        courseId.toString());
  }

  // =========================================================
  // FIND ALL
  // =========================================================

  @Override
  public Mono<PageResult<CourseLocationDto>> findAll(
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
  public Mono<PageResult<CourseLocationDto>> search(
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
                    "name^3",
                    "address",
                    "city",
                    "reference")
                .fuzziness("AUTO")))
        .from(page * size)
        .size(size));

    return executeSearch(
        request,
        page,
        size);
  }

  // =========================================================
  // SEARCH BY CRITERIA
  // =========================================================

  @Override
  public Mono<PageResult<CourseLocationDto>> searchByCriteria(
      String name,
      String city,
      String address,
      UUID courseId,
      int page,
      int size) {

    BoolQuery.Builder bool = new BoolQuery.Builder();

    boolean hasCriteria = false;

    // ---------------------------------------------------------
    // NAME
    // ---------------------------------------------------------

    if (name != null && !name.isBlank()) {

      hasCriteria = true;

      bool.must(m -> m
          .match(mt -> mt
              .field("name")
              .query(name)));
    }

    // ---------------------------------------------------------
    // CITY
    // ---------------------------------------------------------

    if (city != null && !city.isBlank()) {

      hasCriteria = true;

      bool.filter(f -> f
          .term(t -> t
              .field("city.keyword")
              .value(city)));
    }

    // ---------------------------------------------------------
    // ADDRESS
    // ---------------------------------------------------------

    if (address != null && !address.isBlank()) {

      hasCriteria = true;

      bool.must(m -> m
          .match(mt -> mt
              .field("address")
              .query(address)));
    }

    // ---------------------------------------------------------
    // COURSE
    // ---------------------------------------------------------

    if (courseId != null) {

      hasCriteria = true;

      bool.filter(f -> f
          .term(t -> t
              .field("courseId")
              .value(courseId.toString())));
    }

    // ---------------------------------------------------------
    // REQUEST
    // ---------------------------------------------------------

    SearchRequest request;

    if (hasCriteria) {

      request = SearchRequest.of(s -> s
          .index(INDEX)
          .query(q -> q
              .bool(bool.build()))
          .from(page * size)
          .size(size));

    } else {

      request = SearchRequest.of(s -> s
          .index(INDEX)
          .from(page * size)
          .size(size));
    }

    return executeSearch(
        request,
        page,
        size);
  }

  // =========================================================
  // FIND BY CITY
  // =========================================================

  @Override
  public Mono<PageResult<CourseLocationDto>> findByCity(
      String city,
      int page,
      int size) {

    if (city == null || city.isBlank()) {
      return findAll(page, size);
    }

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q
            .term(t -> t
                .field("city.keyword")
                .value(city)))
        .from(page * size)
        .size(size));

    return executeSearch(
        request,
        page,
        size);
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
        .publishOn(Schedulers.boundedElastic())
        .map(response -> {

          if (response.hits().total() == null) {
            return 0L;
          }

          return response.hits()
              .total()
              .value();
        });
  }

  // =========================================================
  // STATISTICS
  // =========================================================

  @Override
  public Mono<CourseLocationStatisticsDto> statistics() {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .size(0)

        .aggregations(
            "withCourses",
            a -> a.filter(
                f -> f.exists(
                    e -> e.field("courseId"))))

        .aggregations(
            "totalCapacity",
            a -> a.sum(
                sm -> sm.field("capacity"))));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(Schedulers.boundedElastic())
        .map(response -> {

          long total = response.hits().total() != null
              ? response.hits()
                  .total()
                  .value()
              : 0L;

          Map<String, Aggregate> aggregations = response.aggregations();

          long locationsWithCourses = 0L;

          Aggregate withCourses = aggregations.get("withCourses");

          if (withCourses != null
              && withCourses.isFilter()) {

            locationsWithCourses = withCourses
                .filter()
                .docCount();
          }

          long totalCapacity = 0L;

          Aggregate capacity = aggregations.get("totalCapacity");

          if (capacity != null && capacity.isSum()) {
            totalCapacity = (long) capacity.sum().value();
          }

          return new CourseLocationStatisticsDto(
              total,
              locationsWithCourses,
              totalCapacity);
        });
  }

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  @Override
  public Mono<List<CourseLocationSuggestionDto>> autocomplete(
      String text,
      int limit) {

    if (text == null || text.isBlank()) {
      return Mono.just(List.of());
    }

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .suggest(su -> su
            .suggesters(
                "location-suggest",
                fs -> fs
                    .prefix(text)
                    .completion(c -> c
                        .field("name.suggest")
                        .skipDuplicates(true)
                        .size(limit)))));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(Schedulers.boundedElastic())
        .map(response -> {

          var suggestion = response.suggest()
              .get("location-suggest");

          if (suggestion == null) {
            return List.<CourseLocationSuggestionDto>of();
          }

          return suggestion
              .stream()
              .flatMap(entry -> entry.completion()
                  .options()
                  .stream())

              .map(option -> {

                Map<String, Object> source = asStringObjectMap(
                    option.source());

                Object id = source.get("id");

                Object name = source.get("name");

                Object city = source.get("city");

                if (id == null || name == null) {
                  return null;
                }

                return new CourseLocationSuggestionDto(
                    UUID.fromString(
                        id.toString()),
                    name.toString(),
                    city != null
                        ? city.toString()
                        : null);
              })

              .filter(java.util.Objects::nonNull)

              .limit(limit)

              .collect(Collectors.toList());
        });
  }

  // =========================================================
  // INDEX
  // =========================================================

  @Override
  public Mono<Void> index(
      CourseLocationDto location) {

    return Mono.fromFuture(
        client.index(i -> i
            .index(INDEX)
            .id(location.id().toString())
            .document(
                toDocument(location))))
        .then();
  }

  // =========================================================
  // BULK INDEX
  // =========================================================

  @Override
  public Mono<Void> bulkIndex(
      List<CourseLocationDto> locations) {

    if (locations == null || locations.isEmpty()) {
      return Mono.empty();
    }

    List<BulkOperation> operations = locations.stream()
        .map(location -> BulkOperation.of(
            operation -> operation
                .index(index -> index
                    .index(INDEX)
                    .id(location.id().toString())
                    .document(
                        toDocument(location)))))
        .collect(Collectors.toList());

    BulkRequest request = BulkRequest.of(
        bulk -> bulk
            .operations(operations));

    return Mono.fromFuture(
        client.bulk(request))
        .flatMap(response -> {

          if (response.errors()) {

            return Mono.error(
                new IllegalStateException(
                    "Error realizando bulk index de CourseLocation"));
          }

          return Mono.empty();
        });
  }

  // =========================================================
  // DELETE
  // =========================================================

  @Override
  public Mono<Void> deleteFromIndex(
      UUID locationId) {

    return Mono.fromFuture(
        client.delete(
            DeleteRequest.of(d -> d
                .index(INDEX)
                .id(locationId.toString()))))
        .then();
  }

  // =========================================================
  // HELPERS
  // =========================================================

  private Mono<CourseLocationDto> findSingleByTerm(
      String field,
      String value) {

    SearchRequest request = SearchRequest.of(s -> s
        .index(INDEX)
        .query(q -> q
            .term(t -> t
                .field(field)
                .value(value)))
        .size(1));

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(Schedulers.boundedElastic())
        .mapNotNull(response -> response.hits()
            .hits()
            .stream()
            .findFirst()
            .map(Hit::source)
            .map(this::asStringObjectMap)
            .map(this::toDto)
            .orElse(null));
  }

  // =========================================================
  // PAGINATED SEARCH
  // =========================================================

  private Mono<PageResult<CourseLocationDto>> executeSearch(
      SearchRequest request,
      int page,
      int size) {

    return Mono.fromFuture(
        client.search(
            request,
            Map.class))
        .publishOn(Schedulers.boundedElastic())
        .map(response -> {

          List<CourseLocationDto> items = response.hits()
              .hits()
              .stream()
              .map(Hit::source)
              .map(this::asStringObjectMap)
              .map(this::toDto)
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

  // =========================================================
  // ELASTIC DOCUMENT -> DTO
  // =========================================================

  private CourseLocationDto toDto(
      Map<String, Object> doc) {

    return new CourseLocationDto(

        UUID.fromString(
            required(
                doc,
                "id")),

        optionalUuid(
            doc,
            "courseId"),

        stringValue(
            doc,
            "name"),

        stringValue(
            doc,
            "address"),

        stringValue(
            doc,
            "city"),

        stringValue(
            doc,
            "reference"),

        integerValue(
            doc,
            "capacity"));
  }

  // =========================================================
  // DTO -> ELASTIC DOCUMENT
  // =========================================================

  private Map<String, Object> toDocument(
      CourseLocationDto location) {

    Map<String, Object> document = new HashMap<>();

    document.put(
        "id",
        location.id().toString());

    if (location.courseId() != null) {
      document.put(
          "courseId",
          location.courseId().toString());
    }

    document.put(
        "name",
        location.name());

    document.put(
        "address",
        location.address());

    document.put(
        "city",
        location.city());

    document.put(
        "reference",
        location.reference());

    document.put(
        "capacity",
        location.capacity());

    return document;
  }

  // =========================================================
  // MAP HELPERS
  // =========================================================

  @SuppressWarnings("unchecked")
  private Map<String, Object> asStringObjectMap(
      Object source) {

    if (source instanceof Map<?, ?> map) {
      return (Map<String, Object>) map;
    }

    throw new IllegalStateException(
        "El documento de Elasticsearch no tiene formato Map");
  }

  private String required(
      Map<String, Object> document,
      String field) {

    Object value = document.get(field);

    if (value == null) {

      throw new IllegalStateException(
          "Campo requerido '" + field
              + "' no existe en documento Elasticsearch");
    }

    return value.toString();
  }

  private String stringValue(
      Map<String, Object> document,
      String field) {

    Object value = document.get(field);

    return value != null
        ? value.toString()
        : null;
  }

  private UUID optionalUuid(
      Map<String, Object> document,
      String field) {

    Object value = document.get(field);

    return value != null
        ? UUID.fromString(value.toString())
        : null;
  }

  private Integer integerValue(
      Map<String, Object> document,
      String field) {

    Object value = document.get(field);

    if (value == null) {
      return null;
    }

    if (value instanceof Number number) {
      return number.intValue();
    }

    return Integer.valueOf(
        value.toString());
  }
}
