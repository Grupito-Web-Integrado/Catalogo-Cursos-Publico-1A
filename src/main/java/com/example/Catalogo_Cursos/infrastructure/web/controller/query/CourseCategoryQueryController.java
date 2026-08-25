package com.example.Catalogo_Cursos.infrastructure.web.controller.query;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.query.courseCategory.AutocompleteCourseCategoriesQuery;
import com.example.Catalogo_Cursos.application.query.courseCategory.CountCourseCategoriesQuery;
import com.example.Catalogo_Cursos.application.query.courseCategory.FindActiveCourseCategoriesQuery;
import com.example.Catalogo_Cursos.application.query.courseCategory.FindCourseCategoriesByStatusQuery;
import com.example.Catalogo_Cursos.application.query.courseCategory.FindInactiveCourseCategoriesQuery;
import com.example.Catalogo_Cursos.application.query.courseCategory.GetAllCourseCategoriesQuery;
import com.example.Catalogo_Cursos.application.query.courseCategory.GetCourseCategoryByIdQuery;
import com.example.Catalogo_Cursos.application.query.courseCategory.GetCourseCategoryByNameQuery;
import com.example.Catalogo_Cursos.application.query.courseCategory.SearchCourseCategoriesQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryBus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-categories")
public class CourseCategoryQueryController {

  private final QueryBus queryBus;

  public CourseCategoryQueryController(
      QueryBus queryBus) {

    this.queryBus = queryBus;
  }

  // =========================================================
  // GET BY ID
  // =========================================================

  @GetMapping("/{id}")
  public Mono<CourseCategoryDto> findById(
      @PathVariable UUID id) {

    return queryBus.dispatch(
        new GetCourseCategoryByIdQuery(id));
  }

  // =========================================================
  // GET BY NAME
  // =========================================================

  @GetMapping("/name/{name}")
  public Mono<CourseCategoryDto> findByName(
      @PathVariable String name) {

    return queryBus.dispatch(
        new GetCourseCategoryByNameQuery(name));
  }

  // =========================================================
  // SEARCH GENERAL
  // =========================================================

  @GetMapping
  public Mono<PageResult<CourseCategoryDto>> search(
      @RequestParam String text,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new SearchCourseCategoriesQuery(
            text,
            page,
            size));
  }

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  @GetMapping("/autocomplete")
  public Mono<List<CourseCategoryDto>> autocomplete(
      @RequestParam String text,
      @RequestParam Integer limit) {

    return queryBus.dispatch(
        new AutocompleteCourseCategoriesQuery(
            text,
            limit));
  }

  // =========================================================
  // COUNT
  // =========================================================

  @GetMapping("/count")
  public Mono<Long> count() {

    return queryBus.dispatch(
        new CountCourseCategoriesQuery());
  }

  // =========================================================
  // GET ALL
  // =========================================================

  @GetMapping("/all")
  public Mono<PageResult<CourseCategoryDto>> findAll(
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new GetAllCourseCategoriesQuery(
            page,
            size));
  }

  // =========================================================
  // ACTIVE
  // =========================================================

  @GetMapping("/active")
  public Mono<PageResult<CourseCategoryDto>> findActive(
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new FindActiveCourseCategoriesQuery(
            page,
            size));
  }

  // =========================================================
  // INACTIVE
  // =========================================================

  @GetMapping("/inactive")
  public Mono<PageResult<CourseCategoryDto>> findInactive(
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new FindInactiveCourseCategoriesQuery(
            page,
            size));
  }

  // =========================================================
  // BY STATUS
  // =========================================================

  @GetMapping("/status/{status}")
  public Mono<PageResult<CourseCategoryDto>> findByStatus(
      @PathVariable String status,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new FindCourseCategoriesByStatusQuery(
            status,
            page,
            size));
  }
}
