package com.example.Catalogo_Cursos.infrastructure.web.controller.query;

import com.example.Catalogo_Cursos.application.dto.course.CourseDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseStatisticsDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseSuggestionDto;
import com.example.Catalogo_Cursos.application.dto.course.CourseSummaryDto;

import com.example.Catalogo_Cursos.application.query.AutocompleteCoursesQuery;
import com.example.Catalogo_Cursos.application.query.CountCoursesQuery;
import com.example.Catalogo_Cursos.application.query.FindCoursesByCategoryQuery;
import com.example.Catalogo_Cursos.application.query.FindCoursesByCriteriaQuery;
import com.example.Catalogo_Cursos.application.query.FindCoursesByDateRangeQuery;
import com.example.Catalogo_Cursos.application.query.FindCoursesByModalityQuery;
import com.example.Catalogo_Cursos.application.query.FindCoursesByStatusQuery;
import com.example.Catalogo_Cursos.application.query.FindCoursesOrderedByNameQuery;
import com.example.Catalogo_Cursos.application.query.FindPublishedCoursesQuery;
import com.example.Catalogo_Cursos.application.query.FindRecentCoursesQuery;
import com.example.Catalogo_Cursos.application.query.GetCourseByCodeQuery;
import com.example.Catalogo_Cursos.application.query.GetCourseByIdQuery;
import com.example.Catalogo_Cursos.application.query.GetCourseStatisticsQuery;
import com.example.Catalogo_Cursos.application.query.SearchCoursesQuery;

import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryBus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseQueryController {

  private final QueryBus queryBus;

  public CourseQueryController(
      QueryBus queryBus) {

    this.queryBus = queryBus;
  }

  // =========================================================
  // GET BY ID
  // =========================================================

  @GetMapping("/{id}")
  public Mono<CourseDto> findById(
      @PathVariable UUID id) {

    return queryBus.dispatch(
        new GetCourseByIdQuery(id));
  }

  // =========================================================
  // GET BY CODE
  // =========================================================

  @GetMapping("/code/{code}")
  public Mono<CourseDto> findByCode(
      @PathVariable String code) {

    return queryBus.dispatch(
        new GetCourseByCodeQuery(code));
  }

  // =========================================================
  // SEARCH GENERAL
  // =========================================================

  @GetMapping
  public Mono<PageResult<CourseSummaryDto>> search(
      @RequestParam String text,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new SearchCoursesQuery(
            text,
            page,
            size));
  }

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  @GetMapping("/autocomplete")
  public Mono<List<CourseSuggestionDto>> autocomplete(
      @RequestParam String text,
      @RequestParam Integer limit) {

    return queryBus.dispatch(
        new AutocompleteCoursesQuery(
            text,
            limit));
  }

  // =========================================================
  // COUNT
  // =========================================================

  @GetMapping("/count")
  public Mono<Long> count() {

    return queryBus.dispatch(
        new CountCoursesQuery());
  }

  // =========================================================
  // BY CATEGORY
  // =========================================================

  @GetMapping("/category/{categoryId}")
  public Mono<PageResult<CourseSummaryDto>> findByCategory(
      @PathVariable UUID categoryId,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new FindCoursesByCategoryQuery(
            categoryId,
            page,
            size));
  }

  // =========================================================
  // BY MODALITY
  // =========================================================

  @GetMapping("/modality/{modality}")
  public Mono<PageResult<CourseSummaryDto>> findByModality(
      @PathVariable String modality,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new FindCoursesByModalityQuery(
            modality,
            page,
            size));
  }

  // =========================================================
  // BY STATUS
  // =========================================================

  @GetMapping("/status/{status}")
  public Mono<PageResult<CourseSummaryDto>> findByStatus(
      @PathVariable String status,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new FindCoursesByStatusQuery(
            status,
            page,
            size));
  }

  // =========================================================
  // BY DATE RANGE
  // =========================================================

  @GetMapping("/date-range")
  public Mono<PageResult<CourseSummaryDto>> findByDateRange(
      @RequestParam LocalDate from,
      @RequestParam LocalDate to,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new FindCoursesByDateRangeQuery(
            from,
            to,
            page,
            size));
  }

  // =========================================================
  // ORDERED BY NAME
  // =========================================================

  @GetMapping("/ordered-by-name")
  public Mono<PageResult<CourseSummaryDto>> orderedByName(
      @RequestParam boolean ascending,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new FindCoursesOrderedByNameQuery(
            ascending,
            page,
            size));
  }

  // =========================================================
  // PUBLISHED COURSES
  // =========================================================

  @GetMapping("/published")
  public Mono<PageResult<CourseSummaryDto>> published(
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new FindPublishedCoursesQuery(
            page,
            size));
  }

  // =========================================================
  // RECENT / UPCOMING COURSES
  // =========================================================

  @GetMapping("/recent")
  public Mono<PageResult<CourseSummaryDto>> recent(
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new FindRecentCoursesQuery(
            page,
            size));
  }

  // =========================================================
  // STATISTICS
  // =========================================================

  @GetMapping("/statistics")
  public Mono<CourseStatisticsDto> statistics() {

    return queryBus.dispatch(
        new GetCourseStatisticsQuery());
  }

  // =========================================================
  // ADVANCED SEARCH
  // =========================================================

  @GetMapping("/search-criteria")
  public Mono<PageResult<CourseSummaryDto>> searchByCriteria(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String code,
      @RequestParam(required = false) String modality,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) LocalDate startDateFrom,
      @RequestParam(required = false) LocalDate startDateTo,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new FindCoursesByCriteriaQuery(
            name,
            code,
            modality,
            status,
            startDateFrom,
            startDateTo,
            page,
            size));
  }
}
