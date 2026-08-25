package com.example.Catalogo_Cursos.infrastructure.output.search.elastic.repository;

import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.Catalogo_Cursos.application.dto.course.CourseDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseStatisticsDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseSuggestionDto;

public interface CourseElasticsearchRepository {

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  // Fuente de verdad para el cache-aside de CachedCoursesReadRepository
  // cuando Redis tiene un MISS.

  Mono<CourseDto> findById(UUID id);

  Mono<CourseDto> findByCode(String code);

  // =========================================================
  // BASIC SEARCH
  // =========================================================

  Mono<PageResult<CourseSummaryDto>> search(

      String text,

      int page,

      int size

  );

  Mono<PageResult<CourseSummaryDto>> searchByCriteria(
      String name,
      String code,
      String modality,
      String status,
      LocalDate startDateAfter,
      LocalDate startDateBefore,
      int page,
      int size);
  // =========================================================
  // FILTERS
  // =========================================================

  Mono<PageResult<CourseSummaryDto>> findByCategory(

      UUID categoryId,

      int page,

      int size

  );

  Mono<PageResult<CourseSummaryDto>> findByCategories(

      Set<UUID> categoryIds,

      int page,

      int size

  );

  Mono<PageResult<CourseSummaryDto>> findByModality(

      String modality,

      int page,

      int size

  );

  Mono<PageResult<CourseSummaryDto>> findByStatus(

      String status,

      int page,

      int size

  );

  Mono<PageResult<CourseSummaryDto>> findPublished(

      int page,

      int size

  );

  // =========================================================
  // DATE FILTERS
  // =========================================================

  Mono<PageResult<CourseSummaryDto>> findUpcoming(

      int page,

      int size

  );

  Mono<PageResult<CourseSummaryDto>> findByStartDateRange(

      LocalDate from,

      LocalDate to,

      int page,

      int size

  );

  Mono<PageResult<CourseSummaryDto>> findByEndDateRange(

      LocalDate from,

      LocalDate to,

      int page,

      int size

  );

  // =========================================================
  // CAPACITY
  // =========================================================

  Mono<PageResult<CourseSummaryDto>> findWithAvailableSlots(

      int page,

      int size

  );

  Mono<PageResult<CourseSummaryDto>> findFull(

      int page,

      int size

  );

  // =========================================================
  // PRICE
  // =========================================================

  Mono<PageResult<CourseSummaryDto>> findByPriceRange(

      java.math.BigDecimal minPrice,

      java.math.BigDecimal maxPrice,

      String currency,

      int page,

      int size

  );

  // =========================================================
  // ORDERING
  // =========================================================

  Mono<PageResult<CourseSummaryDto>> findOrderedByName(

      boolean ascending,

      int page,

      int size

  );

  Mono<PageResult<CourseSummaryDto>> findOrderedByStartDate(

      boolean ascending,

      int page,

      int size

  );

  Mono<PageResult<CourseSummaryDto>> findOrderedByPrice(

      boolean ascending,

      int page,

      int size

  );

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  Mono<List<CourseSuggestionDto>> autocomplete(

      String text,

      int limit

  );

  // =========================================================
  // STATISTICS
  // =========================================================

  Mono<Long> count();

  Mono<CourseStatisticsDto> statistics();

  // =========================================================
  // LIST
  // =========================================================

  // Uso administrativo / procesos internos.

  Flux<CourseDto> findAll();

  // =========================================================
  // INDEXACIÓN
  // =========================================================

  Mono<Void> index(

      CourseDto course

  );

  Mono<Void> bulkIndex(

      List<CourseDto> courses

  );

  Mono<Void> deleteFromIndex(

      UUID courseId

  );
}
