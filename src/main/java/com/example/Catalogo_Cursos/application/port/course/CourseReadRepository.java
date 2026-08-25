package com.example.Catalogo_Cursos.application.port.course;

import com.example.Catalogo_Cursos.application.dto.course.CourseDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseStatisticsDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseSuggestionDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CourseReadRepository {

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  Mono<CourseDto> findById(
      UUID id);

  Mono<CourseDto> findByCode(
      String code);

  // =========================================================
  // BASIC SEARCH
  // =========================================================

  Mono<PageResult<CourseSummaryDto>> search(
      String text,
      int page,
      int size);

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

  Mono<PageResult<CourseSummaryDto>> findByModality(
      String modality,
      int page,
      int size);

  Mono<PageResult<CourseSummaryDto>> findByStatus(
      String status,
      int page,
      int size);

  Mono<PageResult<CourseSummaryDto>> findPublished(
      int page,
      int size);

  Mono<PageResult<CourseSummaryDto>> findByCategory(
      UUID categoryId,
      int page,
      int size);

  // =========================================================
  // DATE FILTERS
  // =========================================================

  Mono<PageResult<CourseSummaryDto>> findUpcoming(
      int page,
      int size);

  Mono<PageResult<CourseSummaryDto>> findByStartDateRange(
      LocalDate from,
      LocalDate to,
      int page,
      int size);

  // =========================================================
  // AVAILABILITY
  // =========================================================

  Mono<PageResult<CourseSummaryDto>> findAvailable(
      int page,
      int size);

  // =========================================================
  // ORDERING
  // =========================================================

  Mono<PageResult<CourseSummaryDto>> findOrderedByName(
      boolean ascending,
      int page,
      int size);

  Mono<PageResult<CourseSummaryDto>> findOrderedByStartDate(
      boolean ascending,
      int page,
      int size);

  Mono<PageResult<CourseSummaryDto>> findOrderedByPrice(
      boolean ascending,
      int page,
      int size);

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  Mono<List<CourseSuggestionDto>> autocomplete(
      String text,
      int limit);

  // =========================================================
  // STATISTICS
  // =========================================================

  Mono<Long> count();

  Mono<CourseStatisticsDto> statistics();

  // =========================================================
  // ADMIN
  // =========================================================

  Flux<CourseDto> findAll();
}
